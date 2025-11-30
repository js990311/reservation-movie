package com.rejs.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import org.springframework.restdocs.payload.FieldDescriptor;

public class DocsUtils  {
    public static FieldDescriptor[] mergeFields(FieldDescriptors fields){
        return fields.getFieldDescriptors().toArray(new FieldDescriptor[0]);
    }
}
