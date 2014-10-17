package com.javahacks.demo.model.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import com.javahacks.demo.model.Container;

public class FoldedItemProvider extends ItemProviderAdapter implements IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider,
		IItemPropertySource, IEditingDomainItemProvider {

	private Map<Object, List<FoldedItemProvider>> map = new WeakHashMap<Object, List<FoldedItemProvider>>();

	private final int DEFAULT_FOLDER_SIZE = -1;

	private int startIndex;
	private int endIndex;
	private Collection<?> originalChildElements;

	public FoldedItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	public FoldedItemProvider(AdapterFactory adapterFactory, int startIndex, int endIndex, List<?> originalChildElements) {
		this(adapterFactory);
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.originalChildElements = originalChildElements;
	}

	public Collection<?> getChildrenSubList() {
		return ((List<?>) originalChildElements).subList(startIndex, endIndex);
	}

	@Override
	public Collection<?> getChildren(Object object) {

		if (object instanceof FoldedItemProvider) {
			return ((FoldedItemProvider) object).getChildrenSubList();
		}

		if (object instanceof WrappedObject) {
			return super.getChildren(((WrappedObject) object).getOwner());
		}

		if (getVirtualFolderSize() == -1) {
			return super.getChildren(object);
		}

		if (map.get(object) == null) {

			originalChildElements = super.getChildren(object);

			if (originalChildElements.size() <= getVirtualFolderSize()) {
				return originalChildElements;
			}

			map.put(object, splitChildren(originalChildElements, (EObject) object));
		}

		return map.get(object);

	}

	private List<FoldedItemProvider> splitChildren(Collection<?> children, EObject parent) {

		List<FoldedItemProvider> vList = new ArrayList<FoldedItemProvider>();

		int ceil = (int) Math.ceil(children.size() / (double) getVirtualFolderSize());

		for (int i = 0; i < ceil; i++) {

			int startIndex = i * getVirtualFolderSize();
			int endIndex = Math.min(startIndex + getVirtualFolderSize(), children.size());

			FoldedItemProvider provider = new FoldedItemProvider(adapterFactory, startIndex, endIndex, (List<?>) children);
			parent.eAdapters().add(provider);
			vList.add(provider);

		}

		return vList;

	}

	@Override
	public Object getParent(Object object) {

		if (object instanceof FoldedItemProvider) {
			return ((FoldedItemProvider) object).target;
		}

		if (object instanceof WrappedObject) {
			return super.getParent(((WrappedObject) object).getOwner());
		}

		Object parent = super.getParent(object);

		Object adapter = adapterFactory.adapt(parent, ITreeItemContentProvider.class);

		if (adapter instanceof FoldedItemProvider) {

			FoldedItemProvider vlfProvider = (FoldedItemProvider) adapter;

			vlfProvider.getChildren(parent); // init maps

			if (vlfProvider.getVirtualFolderSize() > -1 && vlfProvider.map.get(parent) != null) {

				for (FoldedItemProvider child : vlfProvider.map.get(parent)) {

					if (child.getChildrenSubList().contains(object)) {

						return child;
					}
				}

			}

		}

		return parent;
	}

	@Override
	public Object getImage(Object object) {
		return getResourceLocator().getImage("virtual_folder");
	}

	/**
	 * TODO: Remove this
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return AEditPlugin.INSTANCE;
	}

	@Override
	public String getText(Object object) {

		return "[" + startIndex + " ... " + (endIndex - 1) + "]";
	}

	@Override
	public void dispose() {
		originalChildElements = null;

		map.clear();
		super.dispose();
	}

	@Override
	protected void updateChildren(Notification notification) {

		if (notification.getEventType() == Notification.ADD_MANY || notification.getEventType() == Notification.ADD
				|| notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.REMOVE_MANY
				|| notification.getEventType() == Notification.MOVE) {

			map.remove(notification.getNotifier());
		}
		super.updateChildren(notification);
	}

	/**
	 * This returns the virtual folder size. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public int getVirtualFolderSize() {
		return DEFAULT_FOLDER_SIZE;
	}

	@Override
	public Command createCommand(Object object, EditingDomain domain, Class<? extends Command> commandClass, CommandParameter commandParameter) {

		if (object instanceof FoldedItemProvider) {
			object = ((FoldedItemProvider) object).target;
			Object parent = super.getParent(object);

			IEditingDomainItemProvider adapter = (IEditingDomainItemProvider) adapterFactory.adapt(parent, IEditingDomainItemProvider.class);
			return adapter.createCommand(object, domain, commandClass, commandParameter);
		}

		return super.createCommand(object, domain, commandClass, commandParameter);

	}

}
