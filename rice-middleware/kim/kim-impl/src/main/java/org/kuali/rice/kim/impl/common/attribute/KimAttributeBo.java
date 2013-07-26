package org.kuali.rice.kim.impl.common.attribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;
import org.kuali.rice.krad.data.platform.generator.Sequence;

@Entity
@Sequence(name="KRIM_ATTR_DEFN_ID_S", property="id")
@Customizer(EclipseLinkSequenceCustomizer.class)
@Table(name="KRIM_ATTR_DEFN_T")
public class KimAttributeBo extends PersistableBusinessObjectBase implements KimAttributeContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="KIM_ATTR_DEFN_ID")
    private String id;

    @Column(name="CMPNT_NM")
    private String componentName;

    @Column(name="NM")
    private String attributeName;

    @Column(name="NMSPC_CD")
    private String namespaceCode;

    @Column(name="LBL")
    private String attributeLabel;

    @Column(name="ACTV_IND")
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    private boolean active;


    public static KimAttribute to(KimAttributeBo bo) {
        if (bo == null) {
            return null;
        }

        return KimAttribute.Builder.create(bo).build();
    }

    public static KimAttributeBo from(KimAttribute im) {
        if (im == null) {
            return null;
        }

        KimAttributeBo bo = new KimAttributeBo();
        bo.setId(im.getId());
        bo.setComponentName(im.getComponentName());
        bo.setAttributeName(im.getAttributeName());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setAttributeLabel(im.getAttributeLabel());
        bo.setActive(im.isActive());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
