package com.mayuwan.miaosha.validator;

import com.mayuwan.miaosha.utils.ValidateUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String>{
    private boolean required;
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(!required){
           if(StringUtils.isBlank(s)){return true;}
        }
        return ValidateUtil.isMobile(s);
    }

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }
}
