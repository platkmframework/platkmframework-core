package org.platkmframework.comon.service.validator;

import java.util.Set;

import org.platkmframework.comon.service.exception.ServiceException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

public class ValidatorUtil {
	
	private static ValidatorFactory factory = null;
	
	
	public static void checkValidation(Object objBean) throws ServiceException {
		if(factory == null)
			factory = Validation.byDefaultProvider().configure().buildValidatorFactory();
		Set<ConstraintViolation<Object>> constraintViolations = factory.getValidator().validate(objBean);
		if(constraintViolations.size() > 0) {
			String msg = "";
			String coma = "";
			for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
				msg+= coma + constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage();
				coma=", ";
			}
			throw new ServiceException(msg);
		}
	}

}
