package com.javahacks.demo.workbench;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.ui.addon.PartitionedContentProvider;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.javahacks.demo.model.Container;
import com.javahacks.demo.model.Item;
import com.javahacks.demo.model.ModelFactory;

public class View extends ViewPart {

	public static final String ID = "com.javahacks.demo.workbench.view";
	private TreeViewer viewer;

	private AdapterFactoryEditingDomain domain;

	public void createPartControl(Composite parent) {

		final AdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		domain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack());

		createViewer(parent, adapterFactory);
		createViewer(parent, adapterFactory);

	}

	private void createViewer(Composite parent, final AdapterFactory adapterFactory) {

		viewer = new TreeViewer(parent, SWT.FLAT);
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.addDragSupport(DND.DROP_MOVE, new Transfer[] { LocalTransfer.getInstance() }, new ViewerDragAdapter(viewer));
		viewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { LocalTransfer.getInstance() }, new EditingDomainViewerDropAdapter(domain, viewer));

		// we limit the maximal child elements for containers to 10
		viewer.setContentProvider(new PartitionedContentProvider(adapterFactory) {

			@Override
			protected int getVirtualFolderSize(Object folder) {
				return folder instanceof Container ? 10 : DEFAULT_FOLDER_SIZE;
			}
		});

		viewer.setInput(createDemoModel());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		((IDisposable) domain.getAdapterFactory()).dispose();
	}

	private final Item createDemoModel() {

		Container root = ModelFactory.eINSTANCE.createContainer();
		root.setName("Root");

		for (int i = 0; i < 10; i++) {

			Container parent = ModelFactory.eINSTANCE.createContainer();
			parent.setName("Container " + i);

			for (int y = 0; y < 100; y++) {

				Item child = ModelFactory.eINSTANCE.createItem();
				child.setName("Child " + y);
				parent.getItems().add(child);

			}

			root.getItems().add(parent);

		}

		return root;

	}

}