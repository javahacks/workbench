package com.javahacks.demo.model.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.OverrideableCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

public abstract class WrappedEditingDomain implements EditingDomain{

	
	private EditingDomain domain;
	
	public WrappedEditingDomain(EditingDomain domain) {		
		this.domain = domain;
	}



	public Resource createResource(String fileNameURI) {
		return domain.createResource(fileNameURI);
	}

	public Resource loadResource(String fileNameURI) {
		return domain.loadResource(fileNameURI);
	}

	public ResourceSet getResourceSet() {
		return domain.getResourceSet();
	}

	public Command createCommand(Class<? extends Command> commandClass, CommandParameter commandParameter) {
		return domain.createCommand(commandClass, commandParameter);
	}

	public Command createOverrideCommand(OverrideableCommand command) {
		return domain.createOverrideCommand(command);
	}

	public CommandStack getCommandStack() {
		return domain.getCommandStack();
	}

	public Collection<?> getChildren(Object object) {
		return domain.getChildren(object);
	}

	public Object getParent(Object object) {
		return domain.getParent(object);
	}

	public Object getRoot(Object object) {
		return domain.getRoot(object);
	}

	public Collection<?> getNewChildDescriptors(Object object, Object sibling) {
		return domain.getNewChildDescriptors(object, sibling);
	}

	public TreeIterator<?> treeIterator(Object object) {
		return domain.treeIterator(object);
	}

	public List<?> getTreePath(Object object) {
		return domain.getTreePath(object);
	}

	public Collection<Object> getClipboard() {
		return domain.getClipboard();
	}

	public void setClipboard(Collection<Object> clipboard) {
		domain.setClipboard(clipboard);
	}

	public boolean getOptimizeCopy() {
		return domain.getOptimizeCopy();
	}

	public boolean isReadOnly(Resource resource) {
		return domain.isReadOnly(resource);
	}

	public boolean isControllable(Object object) {
		return domain.isControllable(object);
	}
	
	
}
