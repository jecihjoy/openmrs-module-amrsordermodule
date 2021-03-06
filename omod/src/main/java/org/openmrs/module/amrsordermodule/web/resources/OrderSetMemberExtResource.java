package org.openmrs.module.amrsordermodule.web.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsordermodule.api.services.OrderSetExtService;
import org.openmrs.module.amrsordermodule.models.OrderSetExt;
import org.openmrs.module.amrsordermodule.models.OrderSetMemberExt;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

@SubResource(parent = OrderSetExtResource.class, path = "mordersetmember", supportedClass = OrderSetMemberExt.class, supportedOpenmrsVersions = { "2.0.*, 2.1.*, 2.2.*, 2.4.*, 2.8.*" })
public class OrderSetMemberExtResource extends DelegatingSubResource<OrderSetMemberExt, OrderSetExt, OrderSetExtResource> {
	
	Log log = LogFactory.getLog(OrderSetMemberExtResource.class);
	
	@Override
	public boolean hasTypesDefined() {
		return super.hasTypesDefined();
	}
	
	@Override
	public OrderSetMemberExt getByUniqueId(String uuid) {
		return Context.getService(OrderSetExtService.class).getOrderSetMemberByUuid(uuid);
	}
	
	@Override
	public OrderSetMemberExt newDelegate() {
		return new OrderSetMemberExt();
	}
	
	@Override
	protected void delete(OrderSetMemberExt orderSetMemberExt, String s, RequestContext requestContext)
	        throws ResponseException {
		OrderSetExt orderSet = orderSetMemberExt.getOrderSet();
		orderSet.retireOrderSetMember(orderSetMemberExt);
		Context.getService(OrderSetExtService.class).saveOrderSet(orderSet);
		
	}
	
	@Override
	public OrderSetMemberExt save(OrderSetMemberExt delegate) {
		log.error("SAVING ORDER SET MEMBERS" + delegate.toString());
		OrderSetExt parent = delegate.getOrderSet();
		parent.addOrderSetMember(delegate);
		Context.getService(OrderSetExtService.class).saveOrderSet(parent);
		return delegate;
	}
	
	@Override
	public void purge(OrderSetMemberExt orderSetMemberExt, RequestContext requestContext) throws ResponseException {
		OrderSetExt orderSet = orderSetMemberExt.getOrderSet();
		orderSet.removeOrderSetMember(orderSetMemberExt);
		Context.getService(OrderSetExtService.class).saveOrderSet(orderSet);
	}
	
	@Override
	public OrderSetExt getParent(OrderSetMemberExt instance) {
		return instance.getOrderSet();
	}
	
	@Override
	public void setParent(OrderSetMemberExt orderSetMemberExt, OrderSetExt orderSetExt) {
		orderSetMemberExt.setOrderSet(orderSetExt);
	}
	
	@Override
	public PageableResult doGetAll(OrderSetExt parent, RequestContext requestContext) throws ResponseException {
		List<OrderSetMemberExt> orderSetMembers = new ArrayList<OrderSetMemberExt>();
		if (parent != null) {
			for (OrderSetMemberExt orderSetMember : parent.getOrderSetMembers()) {
				orderSetMembers.add(orderSetMember);
			}
		}
		return new NeedsPaging<OrderSetMemberExt>(orderSetMembers, requestContext);
	}
	
	@PropertyGetter("display")
	public String getDisplayString(OrderSetMemberExt orderSetMember) {
		return orderSetMember.getDescription();
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		return getCreatableProperties();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		if (rep instanceof DefaultRepresentation) {
			description.addProperty("orderSetMemberId");
			description.addProperty("uuid");
			description.addProperty("orderType", Representation.REF);
			//			description.addProperty("setMemberType", Representation.REF);
			description.addProperty("orderTemplate");
			description.addProperty("orderTemplateType");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + "full");
			return description;
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("orderSetMemberId");
			description.addProperty("uuid");
			//			description.addProperty("orderType", Representation.REF);
			description.addProperty("setMemberType", Representation.REF);
			description.addProperty("orderTemplate");
			description.addProperty("orderTemplateType");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			description.addProperty("auditInfo");
			return description;
		} else if (rep instanceof RefRepresentation) {
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("setMemberType");
		description.addProperty("concept");
		description.addProperty("orderType");
		description.addProperty("orderTemplate");
		description.addProperty("concept");
		//		description.addProperty("retired");
		return description;
	}
	
}
