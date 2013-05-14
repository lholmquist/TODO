package org.aerogear.todo.server.model;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author edewit@redhat.com
 */
public class TaskTest {
  // javascript test...
  //
  // var test = {"id":1,"name":"tag","tags":[10000000000000001]};
  // console.log(test.tags[0]);

  @Test
  public void shouldSerializeStringsToLong() throws IOException {
    // given
    final ObjectMapper om = new ObjectMapper();
    om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // when
    Task task = om.readValue("{\"id\":\"1\",\"name\":\"tag\",\"tags\":[\"10000000000000001\"],\"project\":\"12\"}", Task.class);

    // then
    assertNotNull(task);
    final List<Tag> tags = task.getTags();
    assertEquals(1, tags.size());
    assertEquals(new Long(10000000000000001L), tags.get(0).getId());
  }

  @Test
  public void shouldDeserializeUsingStringsForLongs() throws IOException {
    // given
    final ObjectMapper om = new ObjectMapper();
    om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // when
    final String jackson = om.writeValueAsString(new Task(200L));

    // then
    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    assertEquals("{\"id\":\"200\",\"title\":null,\"description\":null,\"date\":\"" + date + "\",\"tags\":[]}", jackson);
  }
}
