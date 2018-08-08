/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2018 Serge Rider (serge@jkiss.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.tools.transfer.stream;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.struct.DBSEntity;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.tools.transfer.IDataTransferSettings;
import org.jkiss.dbeaver.tools.transfer.wizard.DataTransferSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stream transfer settings
 */
public class StreamProducerSettings implements IDataTransferSettings {

    public static class EntityMapping {
        private String entityName;
        private DBSEntity entity;
        private List<AttributeMapping> attributeMappings = new ArrayList<>();

        public EntityMapping(DBSEntity entity) {
            this.entity = entity;
            this.entityName = DBUtils.getObjectFullName(entity, DBPEvaluationContext.DML);
        }

        public String getEntityName() {
            return entityName;
        }

        public DBSEntity getEntity() {
            return entity;
        }

        public List<AttributeMapping> getAttributeMappings() {
            return attributeMappings;
        }

        public void setAttributeMappings(List<AttributeMapping> attributeMappings) {
            this.attributeMappings = attributeMappings;
        }

        public AttributeMapping getAttributeMapping(DBSEntityAttribute attr) {
            for (AttributeMapping am : attributeMappings) {
                if (attr.getName().equals(am.getTargetAttributeName())) {
                    return am;
                }
            }
            AttributeMapping am = new AttributeMapping(attr);
            attributeMappings.add(am);
            return am;
        }

        public boolean isComplete() {
            for (AttributeMapping am : attributeMappings) {
                if (am.getMappingType() == AttributeMapping.MappingType.NONE) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class AttributeMapping {

        public static enum MappingType {
            NONE("none"),
            IMPORT("import"),
            DEFAULT_VALUE("default"),
            SKIP("skip");

            private final String title;

            MappingType(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }
        }

        private DBSEntityAttribute targetAttribute;
        private String targetAttributeName;
        private String sourceAttributeName;
        private int sourceAttributeIndex = -1;
        private boolean skip;
        private String defaultValue;
        private MappingType mappingType = MappingType.NONE;

        public AttributeMapping(DBSEntityAttribute attr) {
            this.targetAttribute = attr;
            this.targetAttributeName = attr.getName();
        }

        public MappingType getMappingType() {
            return mappingType;
        }

        public void setMappingType(MappingType mappingType) {
            this.mappingType = mappingType;
        }

        public DBSEntityAttribute getTargetAttribute() {
            return targetAttribute;
        }

        public String getSourceAttributeName() {
            return sourceAttributeName;
        }

        public void setSourceAttributeName(String sourceAttributeName) {
            this.sourceAttributeName = sourceAttributeName;
        }

        public int getSourceAttributeIndex() {
            return sourceAttributeIndex;
        }

        public void setSourceAttributeIndex(int sourceAttributeIndex) {
            this.sourceAttributeIndex = sourceAttributeIndex;
        }

        public String getTargetAttributeName() {
            return targetAttributeName;
        }

        public void setTargetAttributeName(String targetAttributeName) {
            this.targetAttributeName = targetAttributeName;
        }

        public boolean isSkip() {
            return skip;
        }

        public void setSkip(boolean skip) {
            this.skip = skip;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    private Map<String, EntityMapping> entityMapping = new HashMap<>();

    public EntityMapping getEntityMapping(DBSEntity entity) {
        String fullName = DBUtils.getObjectFullName(entity, DBPEvaluationContext.DML);
        EntityMapping mapping = this.entityMapping.get(fullName);
        if (mapping == null) {
            mapping = new EntityMapping(entity);
            entityMapping.put(fullName, mapping);
        }
        return mapping;
    }

    @Override
    public void loadSettings(IRunnableContext runnableContext, DataTransferSettings dataTransferSettings, IDialogSettings dialogSettings) {
    }

    @Override
    public void saveSettings(IDialogSettings dialogSettings) {
    }

}