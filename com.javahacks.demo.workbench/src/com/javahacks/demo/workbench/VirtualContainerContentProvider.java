package com.javahacks.demo.workbench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Wolfgang Geck
 */
public abstract class VirtualContainerContentProvider extends AdapterFactoryContentProvider {

	private final Image descriptor = new Image(Display.getCurrent(), getClass().getResourceAsStream("virtual_folder.gif"));
	protected final int DEFAULT_FOLDER_SIZE = -1;

	private final Map<Object, FoldedItemProvider[]> map = new WeakHashMap<Object, FoldedItemProvider[]>();

	public VirtualContainerContentProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object[] getChildren(Object object) {

		if (object instanceof FoldedItemProvider) {
			return ((FoldedItemProvider) object).getChildrenSubList();
		}

		Object[] children = super.getChildren(object);

		int virtualFolderSize = getVirtualFolderSize(object);

		if (virtualFolderSize <= 0 || children.length <= virtualFolderSize) {
			return children;
		}

		if (map.get(object) == null) {

			map.put(object, splitChildren(children, (EObject) object, virtualFolderSize));
		}

		return map.get(object);

	}

	private FoldedItemProvider[] splitChildren(Object[] children, EObject parent, int folderSize) {

		final List<FoldedItemProvider> vList = new ArrayList<FoldedItemProvider>();

		int ceil = (int) Math.ceil(children.length / (double) folderSize);

		for (int i = 0; i < ceil; i++) {

			int startIndex = i * folderSize;
			int endIndex = Math.min(startIndex + folderSize, children.length);

			FoldedItemProvider provider = new FoldedItemProvider(adapterFactory, startIndex, endIndex, children);
			parent.eAdapters().add(provider);
			vList.add(provider);

		}

		return vList.toArray(new FoldedItemProvider[vList.size()]);

	}

	@Override
	public Object getParent(Object object) {

		Object parent = super.getParent(object);

		if (getVirtualFolderSize(parent) > -1) {

			if (map.get(parent) != null) {

				for (FoldedItemProvider provider : map.get(parent)) {

					for (Object child : provider.getChildrenSubList()) {

						if (child == object)
							return provider;
					}

				}

			}

		}

		return super.getParent(object);
	}

	@Override
	public void notifyChanged(Notification notification) {

		if (notification.getEventType() == Notification.ADD_MANY || notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.REMOVE
				|| notification.getEventType() == Notification.REMOVE_MANY || notification.getEventType() == Notification.MOVE) {

			map.remove(notification.getNotifier());
		}

		super.notifyChanged(notification);
	}

	@Override
	public void dispose() {
		map.clear();
		descriptor.dispose();
		super.dispose();
	}

	protected abstract int getVirtualFolderSize(Object folder);

	private final class FoldedItemProvider extends ItemProviderAdapter implements IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource, IEditingDomainItemProvider {

		private int startIndex;
		private int endIndex;
		private Object[] originalChildElements;

		public FoldedItemProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		public FoldedItemProvider(AdapterFactory adapterFactory, int startIndex, int endIndex, Object[] originalChildElements) {
			this(adapterFactory);
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.originalChildElements = originalChildElements;
		}

		public Object[] getChildrenSubList() {
			return Arrays.copyOfRange(originalChildElements, startIndex, endIndex);
		}

		@Override
		public boolean hasChildren(Object object) {

			return object instanceof FoldedItemProvider || super.hasChildren(object);
		}

		@Override
		public Object getParent(Object object) {

			if (object instanceof FoldedItemProvider) {
				return ((FoldedItemProvider) object).target;
			}

			return super.getParent(object);
		}

		@Override
		public Object getImage(Object object) {
			return descriptor;
		}

		@Override
		public String getText(Object object) {

			return "[" + startIndex + " ... " + (endIndex - 1) + "]";
		}

		@Override
		public void dispose() {
			originalChildElements = null;
			super.dispose();
		}

	}

}
