/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 package eu.udig.style.jgrass.categories;


/**
 * This object holds everything needed to create a {@link CategoryRuleComposite}. This is needed
 * since {@link CategoryRuleComposite} have to be disposed when cleared and recreated when needed
 * again.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class CategoryRule {
    private String value = null;
    private String label = null;
    private boolean isActive = true;

    public CategoryRule() {
        this.value = "";
        this.label = "";
        this.isActive = true;
    }

    public CategoryRule( String value, String label, boolean isActive ) {
        this.value = value;
        this.label = label;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive( boolean isActive ) {
        this.isActive = isActive;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * @return the GRASS definition of a category rule
     */
    public String ruleToString() {
        StringBuffer rule = new StringBuffer();
        rule.append(value).append(":").append(label);
        return rule.toString();
    }

}
