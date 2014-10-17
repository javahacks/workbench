package com.javahacks.demo.workbench;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
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
	private Item child;
	private Container root;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		final AdapterFactory adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		domain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
				new BasicCommandStack());

		viewer = new TreeViewer(parent, SWT.FLAT);

		viewer.setContentProvider(new VirtualContainerContentProvider(
				adapterFactory) {
			@Override
			protected int getVirtualFolderSize(Object folder) {

				return folder instanceof Container ? 10 : DEFAULT_FOLDER_SIZE;
			}
		});
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));

		viewer.addDragSupport(DND.DROP_MOVE,
				new Transfer[] { LocalTransfer.getInstance() },
				new ViewerDragAdapter(viewer));
		viewer.addDropSupport(DND.DROP_MOVE,
				new Transfer[] { LocalTransfer.getInstance() },
				new EditingDomainViewerDropAdapter(domain, viewer));

		viewer.setInput(createDemoModel());

		viewer.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent event) {

				// viewer.setSelection(new StructuredSelection(child));
				root.getItems().remove(0);
			}
		});

		
		
		new Thread(){
			public void run() {
				
				while(true){
					((Container)root.getItems().get(0)).getItems().remove(0);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
		}.start();
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

		root = ModelFactory.eINSTANCE.createContainer();
		root.setName("Root");

		for (int i = 0; i < 10; i++) {

			Container parent = ModelFactory.eINSTANCE.createContainer();
			parent.setName("Container " + i);

			for (int y = 0; y < 100; y++) {

				child = ModelFactory.eINSTANCE.createItem();
				child.setName("Child " + y);
				parent.getItems().add(child);

			}

			root.getItems().add(parent);

		}

		return root;

	}

}