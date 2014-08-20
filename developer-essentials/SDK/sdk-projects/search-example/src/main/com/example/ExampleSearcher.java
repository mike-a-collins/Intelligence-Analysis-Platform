/*
 * Copyright (c) 2014 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example;

import java.util.HashMap;
import java.util.List;

import com.i2group.apollo.client.*;
import com.i2group.apollo.infoservice.IInfoService;
import com.i2group.apollo.model.data.transport.Card;
import com.i2group.apollo.model.data.transport.Entity;
import com.i2group.apollo.model.data.transport.Items;
import com.i2group.apollo.model.data.transport.Link;
import com.i2group.apollo.model.data.transport.Property;
import com.i2group.apollo.model.data.transport.PropertyGroup;
import com.i2group.apollo.model.schema.transport.EntityType;
import com.i2group.apollo.model.schema.transport.LinkType;
import com.i2group.apollo.model.schema.transport.PropertyGroupType;
import com.i2group.apollo.model.schema.transport.PropertyType;
import com.i2group.apollo.searchservice.ISearchService;
import com.i2group.apollo.searchservice.StringArgumentTooLongException;
import com.i2group.apollo.searchservice.transport.PageInfo;
import com.i2group.apollo.searchservice.transport.PageSelection;
import com.i2group.apollo.searchservice.transport.SearchResults;
import com.i2group.apollo.service.common.InvalidArgumentException;
import com.i2group.apollo.service.common.SetSizeExceededException;
import com.i2group.apollo.service.common.ValidationException;

/**
 * A data loader that converts XML from an external file to platform-compatible
 * format, and uses an {@link AnalysisRepositoryLoader} to manipulate items
 * in the Analysis Repository.
 */
public final class ExampleSearcher
{

	private final IClientFactory repo;
    /**
     * Constructs a new {@link ExampleDataLoader}.
     */
    public ExampleSearcher()
    {
        repo = ClientFactoryFactory.createClientFactory("http://localhost/apollo/services", "Manager", "password");
    }
	
	public void runSearch()
	{
		HashMap<String, String> eSchemaLkup = new HashMap<String, String>();
		eSchemaLkup.clear();
		HashMap<String, String> lSchemaLkup = new HashMap<String, String>();
		lSchemaLkup.clear();
		IInfoService info = repo.createInfoServiceClient();
		String datasid = info.getDataSourcesAndSchema().getMasterDataSource().getId();
		List<EntityType> etypes = info.getDataSourcesAndSchema().getSchema().getItemTypes().getEntityTypes();
		for (EntityType etype : etypes)
		{
			eSchemaLkup.put(etype.getId(), etype.getDisplayName());
			for(PropertyGroupType pgtype : etype.getPropertyGroupTypes())
			{
				eSchemaLkup.put(pgtype.getId(), pgtype.getDisplayName());
				for(PropertyType ptype : pgtype.getPropertyTypes())
				{
					eSchemaLkup.put(ptype.getId(), ptype.getDisplayName());
				}
			}
			for(PropertyType ptype : etype.getPropertyTypes())
			{
				eSchemaLkup.put(ptype.getId(), ptype.getDisplayName());
			}
		}
		List<LinkType> ltypes = info.getDataSourcesAndSchema().getSchema().getItemTypes().getLinkTypes();
		for (LinkType ltype : ltypes)
		{
			lSchemaLkup.put(ltype.getId(), ltype.getDisplayName());
			for(PropertyGroupType pgtype : ltype.getPropertyGroupTypes())
			{
				lSchemaLkup.put(pgtype.getId(), pgtype.getDisplayName());
				for(PropertyType ptype : pgtype.getPropertyTypes())
				{
					lSchemaLkup.put(ptype.getId(), ptype.getDisplayName());
				}
			}
			for(PropertyType ptype : ltype.getPropertyTypes())
			{
				lSchemaLkup.put(ptype.getId(), ptype.getDisplayName());
			}
		}
	    ISearchService searcher = repo.createSearchServiceClient(datasid);
	    try {
	    	System.out.println("<Data>");
			SearchResults retval = searcher.searchAndRetrieve(null, null, null, null, null, 100, new PageInfo(PageSelection.FIRST));
			Items items = retval.getItems();
			for (Entity ent : items.getEntities())
			{
				System.out.println("<" + eSchemaLkup.get(ent.getItemTypeId()) + " id=\""+ ent.getItemIdentifierAndVersion().getItemIdentifier().getKey() + "\">");
				for (Card card : ent.getCards())
				{
				    for (PropertyGroup pg : card.getPropertyGroups())
				    {
				    	System.out.println("<" + eSchemaLkup.get(pg.getPropertyGroupTypeId()) + ">");
				        for (Property p : pg.getProperties())
				        {
				        	System.out.println("<"+eSchemaLkup.get(p.getPropertyTypeId()) +">"+ p.getPropertyValue().getValue().toString() + "</"+eSchemaLkup.get(p.getPropertyTypeId()) +">");
				        }
				        System.out.println("</" + eSchemaLkup.get(pg.getPropertyGroupTypeId()) + ">");
				    }
				    for (Property p : card.getProperties())
				    {
				    	if(p.getPropertyValue() != null)
				    	{
				    	    System.out.println("<"+eSchemaLkup.get(p.getPropertyTypeId()) +">"+ p.getPropertyValue().getValue().toString() + "</"+eSchemaLkup.get(p.getPropertyTypeId()) +">");
				    	}
				    }
				}
				System.out.println("</" + eSchemaLkup.get(ent.getItemTypeId()) + ">");
			}
			for (Link lnk : items.getLinks())
			{
				System.out.println("<" + lSchemaLkup.get(lnk.getItemTypeId()) + " id=\""+ lnk.getItemIdentifierAndVersion().getItemIdentifier().getKey() + "\">");
				System.out.println("<fromId>" + lnk.getFromEntityItemIdentifier().getKey() + "</fromId>");
				System.out.println("<toId>" + lnk.getToEntityItemIdentifier().getKey() + "</toId>");
				System.out.println("<linkDirection>" + lnk.getLinkDirection().toString() + "</linkDirection>");
				for (Card card : lnk.getCards())
				{
				    for (PropertyGroup pg : card.getPropertyGroups())
				    {
				    	System.out.println("<" + lSchemaLkup.get(pg.getPropertyGroupTypeId()) + ">");
				        for (Property p : pg.getProperties())
				        {
				        	System.out.println("<"+lSchemaLkup.get(p.getPropertyTypeId()) +">"+ p.getPropertyValue().getValue().toString() + "</"+lSchemaLkup.get(p.getPropertyTypeId()) +">");
				        }
				        System.out.println("</" + lSchemaLkup.get(pg.getPropertyGroupTypeId()) + ">");
				    }
				    for (Property p : card.getProperties())
				    {
				    	if(p.getPropertyValue() != null)
				    	{
				    	    System.out.println("<"+lSchemaLkup.get(p.getPropertyTypeId()) +">"+ p.getPropertyValue().getValue().toString() + "</"+lSchemaLkup.get(p.getPropertyTypeId()) +">");
				    	}
				    }
				}
				System.out.println("</" + lSchemaLkup.get(lnk.getItemTypeId()) + ">");
			}
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SetSizeExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StringArgumentTooLongException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("</Data>");
	}
}
