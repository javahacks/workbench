package com.javahacks.demo.model;

import org.eclipse.emf.common.util.EList;

/**
 * @model
 */
public interface Container extends Item {

	/**
	 * @model containment="true"
	 */
	public EList<Item> getItems();

}
