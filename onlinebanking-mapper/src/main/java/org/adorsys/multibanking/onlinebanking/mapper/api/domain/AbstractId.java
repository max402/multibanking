package org.adorsys.multibanking.onlinebanking.mapper.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Superclass for all object with an identity.
 * 
 * @author fpo 2018-03-24 01:44
 *
 */
@Data
@EqualsAndHashCode(of={"id"})
public abstract class AbstractId implements IdentityIf {
	private String id;
}
