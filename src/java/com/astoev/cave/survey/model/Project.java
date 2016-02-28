package com.astoev.cave.survey.model;

import com.astoev.cave.survey.util.ConfigUtil;
import com.astoev.cave.survey.util.StringUtils;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: astoev
 * Date: 1/24/12
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
@DatabaseTable(tableName = "projects")
public class Project implements Serializable {

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SKETCH_PLAN = "sketch_plan_id";
    public static final String COLUMN_SKETCH_SECTION = "sketch_section_id";


    @DatabaseField(generatedId = true, canBeNull = false, columnName = "id")
    private Integer mId;
    @DatabaseField(columnName = COLUMN_NAME)
    private String mName;
    @ForeignCollectionField()
    private ForeignCollection<Leg> mLegs;
    @DatabaseField(columnName = "creation_date", dataType = DataType.DATE)
    private Date mCreationDate;
    @DatabaseField(foreign = true, columnName = COLUMN_SKETCH_PLAN) // plan sketch for whole project
    private Sketch mSketchPlan;
    @DatabaseField(foreign = true, columnName = COLUMN_SKETCH_SECTION) // section sketch for whole project
    private Sketch mSketchSection;


    public Project() {

    }


    public Integer getId() {
        return mId;
    }

    public void setId(Integer aId) {
        mId = aId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String aName) {
        mName = aName;
    }

    public ForeignCollection<Leg> getLegs() {
        return mLegs;
    }

    public void setLegs(ForeignCollection<Leg> aLegs) {
        mLegs = aLegs;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date aCreationDate) {
        mCreationDate = aCreationDate;
    }

    public Sketch getSketchPlan() {
        return mSketchPlan;
    }

    public void setSketchPlan(Sketch sketchPlan) {
        mSketchPlan = sketchPlan;
    }

    public Sketch getSketchSection() {
        return mSketchSection;
    }

    public void setSketchSection(Sketch sketchSection) {
        mSketchSection = sketchSection;
    }

    public String getCreationDateFormatted() {


        Locale locale;
        String savedLanguage = ConfigUtil.getStringProperty(ConfigUtil.PREF_LOCALE);
        if (StringUtils.isEmpty(savedLanguage)){
            // use any default formatting
            locale = Locale.getDefault();
        } else {
            // format depending on the locale
            locale = new Locale(savedLanguage);
        }
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        return df.format(mCreationDate);
    }

    /**
     * Returns only the name of the project as we use it inside ArrayAdapter for ListView 
     */
	@Override
	public String toString() {
		return getName();
	}
    
}
