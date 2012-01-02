/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.processor.attr;

import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractAttributeModifierAttrProcessor extends AbstractAttrProcessor {

    public enum ModificationType { SUBSTITUTION, APPEND, APPEND_WITH_SPACE, PREPEND, PREPEND_WITH_SPACE }
    
    
    


    public AbstractAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }





    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Tag tag, final String attributeName) {
        
        final Map<String,String> modifiedAttributeValues = 
            getModifiedAttributeValues(arguments, tag, attributeName); 
        if (modifiedAttributeValues == null) {
            throw new AttrProcessorException(
                    "Null new attribute value map specified for: \"" + attributeName + "\"");
        }
        
        for (final Map.Entry<String,String> modifiedAttributeEntry : modifiedAttributeValues.entrySet()) {

            final String modifiedAttributeName = modifiedAttributeEntry.getKey();
            String modifiedAttributeValue = modifiedAttributeEntry.getValue();
            String currentAttributeValue = tag.getAttributeValue(modifiedAttributeName);
            
            final ModificationType modificationType =
                    getModificationType(arguments, tag, attributeName, modifiedAttributeName);
            
            if (currentAttributeValue == null) {
                currentAttributeValue = "";
            }

            if (modifiedAttributeValue == null) {
                modifiedAttributeValue = "";
            }
            
            
            switch (modificationType) {
                case SUBSTITUTION :
                    break;
                case APPEND :
                    modifiedAttributeValue = currentAttributeValue + modifiedAttributeValue;
                    break;
                case APPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        modifiedAttributeValue = currentAttributeValue + " " + modifiedAttributeValue;
                    } else {
                        modifiedAttributeValue = currentAttributeValue + modifiedAttributeValue;
                    }
                    break;
                case PREPEND :
                    modifiedAttributeValue = modifiedAttributeValue + currentAttributeValue;
                    break;
                case PREPEND_WITH_SPACE :
                    if (!currentAttributeValue.equals("")) {
                        modifiedAttributeValue = modifiedAttributeValue + " " + currentAttributeValue;
                    } else {
                        modifiedAttributeValue = modifiedAttributeValue + currentAttributeValue;
                    }
                    break;
            }


            final boolean removeAttributeIfEmpty =
                removeAttributeIfEmpty(arguments, tag, attributeName, modifiedAttributeName);
            
            // Do NOT use trim() here! Non-thymeleaf attributes set to ' ' could have meaning!
            if (modifiedAttributeValue.equals("") && removeAttributeIfEmpty) {
                tag.removeAttribute(modifiedAttributeName);
            } else {
                tag.setAttribute(modifiedAttributeName, modifiedAttributeValue);
            }
            
        }
        
        tag.removeAttribute(attributeName);
        
        return ProcessorResult.OK;
        
    }

    
    
    protected abstract Map<String,String> getModifiedAttributeValues(final Arguments arguments, 
            final Tag tag, final String attributeName);
    

    
    protected abstract ModificationType getModificationType(final Arguments arguments, 
            final Tag tag, final String attributeName, final String newAttributeName);
    
    
    protected abstract boolean removeAttributeIfEmpty(final Arguments arguments, 
            final Tag tag, final String attributeName, final String newAttributeName);
    
}