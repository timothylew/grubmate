package cs310.fidgetspinners.grubmate;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.ui.CreatePostFragment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by anish on 10/27/17.
 */
public class CreatePostFragmentTest {
    @Test
    public void blankName() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","","type","category","location",null,"description",
                "price",0,0,"startrange","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postNameBlank", result);
    }
    @Test
    public void longName() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","Post Name should always be less than or equal to 50 characters.",
                "type","category","location",null,"description",
                "price",0,0,"startrange","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postNameLong", result);
    }
    @Test
    public void blankDescription() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","name","type","category","location",null,"",
                "price",0,0,"startrange","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postDescriptionBlank", result);
    }
    @Test
    public void blankLocation() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","name","type","category","",null,"description",
                "price",0,0,"startrange","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postLocationBlank", result);
    }
    @Test
    public void blankStartRange() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","name","type","category","location",null,"description",
                "price",0,0,"","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postStartRangeBlank", result);
    }
    @Test
    public void blankEndRange() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","name","type","category","location",null,"description",
                "price",0,0,"startrange","",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postEndRangeBlank", result);
    }
    @Test
    public void noPicturesForHomemade() throws Exception {
        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",null,"description",
                "price",0,0,"startrange","endrange",new ArrayList<String>(), null, 0.0, 0.0,null);
        assertEquals("postNoImage", result);
    }
    @Test
    public void startRangeFormat() throws Exception {
        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",null,"description",
                "price",0,0,"startrange","endrange",images, null, 0.0, 0.0,null);
        assertEquals("dateParsingError", result);
    }
    @Test
    public void endRangeFormat() throws Exception {
        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",null,"description",
                "price",0,0,"10-25-2017 14:30","endrange",images, null, 0.0, 0.0,null);
        assertEquals("dateParsingError", result);
    }
    @Test
    public void startBeforEnd() throws Exception {
        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",null,"description",
                "price",0,0,"10-25-2017 14:30","10-25-2017 13:30",images, null, 0.0, 0.0,null);
        assertEquals("dateMismatchError", result);
    }

    @Test
    public void noErrors() throws Exception {
        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");

        ArrayList<String> tags = new ArrayList<>();

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",tags,"description",
                "price",0,0,"10-25-2017 14:30","10-25-2017 15:30",images, null, 0.0, 0.0, new Post());
        assertEquals("NoErrors", result);
    }

    @Test
    public void fieldsReturnedNotNull() throws Exception {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("First tag");

        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");
        Post returnedPost = new Post();

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",tags,"description",
                "price",0,0,"10-25-2017 14:30","10-25-2017 15:30",images, null, 0.0, 0.0, returnedPost);

        assertNotNull(returnedPost);
        assertNotNull(returnedPost.getOriginalPoster());
        assertNotNull(returnedPost.getPostName());
        assertNotNull(returnedPost.getType());
        assertNotNull(returnedPost.getCategory());
        assertNotNull(returnedPost.getLocation());
        assertNotNull(returnedPost.getTags());
        assertNotNull(returnedPost.getDescription());
        assertNotNull(returnedPost.getPrice());
        assertNotNull(returnedPost.getMaxPortions());
        assertNotNull(returnedPost.getPortionsAvailable());
        assertNotNull(returnedPost.getStartRange());
        assertNotNull(returnedPost.getEndRange());
        assertNotNull(returnedPost.getPictures());
        assertNotNull(returnedPost.getLatitude());
        assertNotNull(returnedPost.getLongitude());
        assertNull(returnedPost.getRequests());
        assertNotNull(result);

    }
    @Test
    public void fieldsReturnedMatchInput() throws Exception {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("First tag");

        ArrayList<String> images = new ArrayList<>();
        images.add("First Image Location");
        Post returnedPost = new Post();

        String result = CreatePostFragment.CreatePost("poster","name","Homemade","category","location",tags,"description",
                "price",0,0,"10-25-2017 14:30","10-25-2017 15:30",images, null, 0.0, 0.0, returnedPost);

        assertEquals("NoErrors", result);
        assertEquals("poster", returnedPost.getOriginalPoster());
        assertEquals("name", returnedPost.getPostName());
        assertEquals("Homemade", returnedPost.getType());
        assertEquals("category", returnedPost.getCategory());
        assertEquals("location", returnedPost.getLocation());
        assertEquals("description", returnedPost.getDescription());
        assertEquals("price", returnedPost.getPrice());
        assertEquals("First tag", returnedPost.getTags().get(0));
        assertEquals("First Image Location", returnedPost.getPictures().get(0));
        assertEquals(0, returnedPost.getMaxPortions());
        assertEquals(0, returnedPost.getPortionsAvailable());
        assertEquals(0.0, returnedPost.getLatitude());
        assertEquals(0.0, returnedPost.getLongitude());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        assertEquals("10-25-2017 14:30", dateFormat.format(returnedPost.getStartRange()));
        assertEquals("10-25-2017 15:30", dateFormat.format(returnedPost.getEndRange()));

    }
}