package ch.epfl.sweng.tutosaurus.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;

import ch.epfl.sweng.tutosaurus.activity.MainActivity;
import ch.epfl.sweng.tutosaurus.R;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by samuel on 06.12.16.
 */


@RunWith(AndroidJUnit4.class)
public class PictureHelperTest {
    private Bitmap icon;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setup() {
        icon = BitmapFactory.decodeResource(
                mActivityRule.getActivity().getApplicationContext().getResources(),
                R.drawable.einstein);
        PictureHelper.storePicLocal(mActivityRule.getActivity(), "profile", icon);
    }
    @After
    public void tearDown () {
        String dirPath = mActivityRule.getActivity().getFilesDir().getAbsolutePath()
                + File.separator + "pictures";
        File file = new File(dirPath + "/profile.png");
        file.delete();
    }


    // The loaded picture == the stored one
    @Test
    public void loadLocalExistingPicTest() {
        Bitmap loadPic = PictureHelper.loadPictureLocal(mActivityRule.getActivity(), "profile");
        assertNotNull(loadPic);
        assert(loadPic.sameAs(icon));
    }

    // Return Null if image does not exist
    @Test
    public void loadLocalFalsePicTest() {
        assertNull(PictureHelper.loadPictureLocal(mActivityRule.getActivity(), "wrongName"));
    }

    @Test
    public void loadLocalNullPicTest() {
        assertNull(PictureHelper.loadPictureLocal(mActivityRule.getActivity(), null));
    }

    // Test if storePicLocal create a folder
    @Test
    public void storePicLocalNewDirTest() {
        String dirPath = mActivityRule.getActivity().getFilesDir().getAbsolutePath() + File.separator + "pictures";
        File projDir = new File(dirPath);
        projDir.delete();

        PictureHelper.storePicLocal(mActivityRule.getActivity(), "profile", icon);
        projDir = new File(dirPath);
        assert(projDir.exists());
    }

    @Test
    public void storePictureOnlineTest() {
        String dirPath = mActivityRule.getActivity().getFilesDir().getAbsolutePath() + File.separator + "pictures";
        PictureHelper.storePictureOnline(dirPath+"/profile.png", "test/profile.png");
    }

    @Test
    public void firstMethodToStoreProfilePictureOnlineTest() {
        String dirPath = mActivityRule.getActivity().getFilesDir().getAbsolutePath() + File.separator + "pictures";
        PictureHelper.storePicOnline(dirPath+"/profile.png", "000000");
    }

    @Test
    public void secondMethodStoreFalsePictureOnlineTest() throws FileNotFoundException {
        PictureHelper.storeProfilePicOnline(mActivityRule.getActivity(), "000000");
    }

}
