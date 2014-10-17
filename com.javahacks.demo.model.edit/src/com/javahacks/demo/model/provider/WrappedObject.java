package com.javahacks.demo.model.provider;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

public class WrappedObject extends EObjectImpl{

	
	private EObject o;

	public WrappedObject(EObject o) {
		super();
		this.o = o;
	}
	
	
	public Object getOwner() {
		return o;
	}
	
	
	@Override
	public EList<Adapter> eAdapters() {		
		return o.eAdapters();
	}


	public boolean eDeliver() {
		return o.eDeliver();
	}


	public void eSetDeliver(boolean deliver) {
		o.eSetDeliver(deliver);
	}


	public void eNotify(Notification notification) {
		o.eNotify(notification);
	}


	public EClass eClass() {
		return o.eClass();
	}


	public Resource eResource() {
		return o.eResource();
	}


	public EObject eContainer() {
		return o.eContainer();
	}


	public EStructuralFeature eContainingFeature() {
		return o.eContainingFeature();
	}


	public EReference eContainmentFeature() {
		return o.eContainmentFeature();
	}


	public EList<EObject> eContents() {
		return o.eContents();
	}


	public TreeIterator<EObject> eAllContents() {
		return o.eAllContents();
	}


	public boolean eIsProxy() {
		return o.eIsProxy();
	}


	public EList<EObject> eCrossReferences() {
		return o.eCrossReferences();
	}


	public Object eGet(EStructuralFeature feature) {
		return o.eGet(feature);
	}


	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return o.eGet(feature, resolve);
	}


	public void eSet(EStructuralFeature feature, Object newValue) {
		o.eSet(feature, newValue);
	}


	public boolean eIsSet(EStructuralFeature feature) {
		return o.eIsSet(feature);
	}


	public void eUnset(EStructuralFeature feature) {
		o.eUnset(feature);
	}


	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		return o.eInvoke(operation, arguments);
	}
	
	
	
}
