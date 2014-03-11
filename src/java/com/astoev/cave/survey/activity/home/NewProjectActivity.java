package com.astoev.cave.survey.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.astoev.cave.survey.Constants;
import com.astoev.cave.survey.R;
import com.astoev.cave.survey.activity.MainMenuActivity;
import com.astoev.cave.survey.activity.UIUtilities;
import com.astoev.cave.survey.activity.main.PointActivity;
import com.astoev.cave.survey.model.*;
import com.astoev.cave.survey.service.Options;
import com.astoev.cave.survey.util.DaoUtil;
import com.astoev.cave.survey.service.orientation.OrientationProcessorFactory;
import com.astoev.cave.survey.util.PointUtil;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: astoev
 * Date: 1/23/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewProjectActivity extends MainMenuActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newproject);

        // populate spinners

        // distance
        prepareSpinner(R.id.options_units_distance, R.array.distance_units);
        prepareSpinner(R.id.options_distance_type, R.array.distance_read_type);

        // azimuth
        prepareSpinner(R.id.options_units_azimuth, R.array.azimuth_units);
        
        // check if build in orientation sensor is available
        boolean hasBuildInOrientationProcessor = OrientationProcessorFactory.canReadOrientation(this);
        int azimuthOptions = R.array.azimuth_read_type_all;
        int slopepeOptions = R.array.slope_read_type_all;
        
        if (!hasBuildInOrientationProcessor){
        	azimuthOptions = R.array.azimuth_read_type_manually;
        	slopepeOptions = R.array.slope_read_type;
        }
        prepareSpinner(R.id.options_azimuth_type, azimuthOptions);

        // slope
        prepareSpinner(R.id.options_units_slope, R.array.slope_units);
        prepareSpinner(R.id.options_slope_type, slopepeOptions);

    }
    
    /**
	 * @see com.astoev.cave.survey.activity.BaseActivity#getScreenTitle()
	 */
	@Override
	protected String getScreenTitle() {
		return getString(R.string.new_title);
	}

	private void prepareSpinner(int aSpinnerId, int aTextArrayId) {
        Spinner spinner = (Spinner) findViewById(aSpinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, aTextArrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private int getSpinnerValue(int aSpinnerId) {
        Spinner spinner = (Spinner) findViewById(aSpinnerId);
        return spinner.getSelectedItemPosition();
    }

    public void createNewProject() {
        try {

            Log.v(Constants.LOG_TAG_UI, "Creating project");

            EditText projectNameField = (EditText) findViewById(R.id.new_projectname);
            final String newProjectName = projectNameField.getText().toString();
            if (newProjectName.trim().equals("")) {
                projectNameField.setError(getString(R.string.new_project_name_required));
                return;
            }

            //check existing
            List<Project> sameNameProjects = getWorkspace().getDBHelper().getProjectDao().queryForEq(Project.COLUMN_NAME, newProjectName);
            if (sameNameProjects.size() > 0) {
                projectNameField.setHint(R.string.home_button_new_exists);
                projectNameField.setError(getString(R.string.home_button_new_exists, newProjectName));
                return;
            }

            Project project = TransactionManager.callInTransaction(getWorkspace().getDBHelper().getConnectionSource(),
                    new Callable<Project>() {
                        public Project call() throws SQLException {

                            // project
                            Project newProject = new Project();
                            newProject.setName(newProjectName);
                            newProject.setCreationDate(new Date());
                            getWorkspace().getDBHelper().getProjectDao().create(newProject);
                            getWorkspace().setActiveProject(newProject);

                            // gallery
                            Gallery firstGallery = DaoUtil.createGallery(true);

                            // points
                            Point startPoint = PointUtil.createFirstPoint();
                            getWorkspace().getDBHelper().getPointDao().create(startPoint);
                            Point secondPoint = PointUtil.createSecondPoint();
                            getWorkspace().getDBHelper().getPointDao().create(secondPoint);

                            // first leg
                            Leg firstLeg = new Leg(startPoint, secondPoint, newProject, firstGallery.getId());
                            getWorkspace().getDBHelper().getLegDao().create(firstLeg);
                            getWorkspace().setActiveLeg(firstLeg);

                            // project units

                            // distance
                            switch (getSpinnerValue(R.id.options_units_distance)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Using meters for distance");
                                    Options.createOption(Option.CODE_DISTANCE_UNITS, Option.UNIT_METERS);
                            }

                            switch (getSpinnerValue(R.id.options_distance_type)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Read distance manually");
                                    Options.createOption(Option.CODE_DISTANCE_SENSOR, Option.CODE_SENSOR_NONE);
                                    break;
                                case 1:
                                    Log.i(Constants.LOG_TAG_UI, "Read distance from BT");
                                    Options.createOption(Option.CODE_DISTANCE_SENSOR, Option.CODE_SENSOR_BLUETOOTH);
                            }

                            // azimuth
                            switch (getSpinnerValue(R.id.options_units_azimuth)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Using degrees for azimuth");
                                    Options.createOption(Option.CODE_AZIMUTH_UNITS, Option.UNIT_DEGREES);
                                    break;
                                case 1:
                                    Log.i(Constants.LOG_TAG_UI, "Using grads for azimuth");
                                    Options.createOption(Option.CODE_AZIMUTH_UNITS, Option.UNIT_GRADS);
                            }

                            switch (getSpinnerValue(R.id.options_azimuth_type)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Read azimuth manually");
                                    Options.createOption(Option.CODE_AZIMUTH_SENSOR, Option.CODE_SENSOR_NONE);
                                    break;
                                case 1:
                                    Log.i(Constants.LOG_TAG_UI, "Read azimuth from build-in sensor");
                                    Options.createOption(Option.CODE_AZIMUTH_SENSOR, Option.CODE_SENSOR_INTERNAL);
                            }

                            // slope
                            switch (getSpinnerValue(R.id.options_units_slope)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Using degrees for slope");
                                    Options.createOption(Option.CODE_SLOPE_UNITS, Option.UNIT_DEGREES);
                                    break;
                                case 1:
                                    Log.i(Constants.LOG_TAG_UI, "Using grads for slope");
                                    Options.createOption(Option.CODE_SLOPE_UNITS, Option.UNIT_GRADS);
                            }

                            switch (getSpinnerValue(R.id.options_slope_type)) {
                                case 0:
                                    Log.i(Constants.LOG_TAG_UI, "Read slope manually");
                                    Options.createOption(Option.CODE_SLOPE_SENSOR, Option.CODE_SENSOR_NONE);
                                    break;
                                case 1:
                                    Log.i(Constants.LOG_TAG_UI, "Read slope from BT");
                                    Options.createOption(Option.CODE_SLOPE_SENSOR, Option.CODE_SENSOR_BLUETOOTH);
                                    break;
                                case 2:
                                    Log.i(Constants.LOG_TAG_UI, "Read slope from build-in sensor");
                                    Options.createOption(Option.CODE_SLOPE_SENSOR, Option.CODE_SENSOR_INTERNAL);
                                    break;
                            }

                            return newProject;
                        }
                    });

            if (project != null) {
                Intent intent = new Intent(NewProjectActivity.this, PointActivity.class);
                getWorkspace().setActiveProject(project);
                getWorkspace().setActiveLeg(getWorkspace().getActiveOrFirstLeg());
                intent.putExtra(Constants.LEG_SELECTED, getWorkspace().getActiveLegId());
                startActivity(intent);
            } else {
                Log.e(Constants.LOG_TAG_DB, "No project created");
                UIUtilities.showNotification(R.string.error);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG_UI, "Failed at new project creation", e);
            UIUtilities.showNotification(R.string.error);
        }
    }

	/**
	 * @see com.astoev.cave.survey.activity.MainMenuActivity#getChildsOptionsMenu()
	 */
	@Override
	protected int getChildsOptionsMenu() {
		return R.menu.newprojectmenu;
	}

	/**
	 * Don't want to see base menu items
	 * 
	 * @see com.astoev.cave.survey.activity.MainMenuActivity#showBaseOptionsMenu()
	 */
	@Override
	protected boolean showBaseOptionsMenu() {
		return false;
	}

	/**
	 * @see com.astoev.cave.survey.activity.MainMenuActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(Constants.LOG_TAG_UI, "NewProject activity's menu selected - " + item.toString());
		
		switch (item.getItemId()) {
			case R.id.new_action_create : {
				createNewProject();
				return true;
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
}