package fr.ouestfrance.querydsl.postgrest;

import lombok.SneakyThrows;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import shaded_package.org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

import static org.mockserver.model.HttpResponse.response;

public class TestUtils {

    public static HttpResponse jsonResponse(String content) {
        return HttpResponse.response().withContentType(MediaType.APPLICATION_JSON)
                .withBody(content);
    }


    public static HttpResponse jsonFileResponse(String resourceFileName) {
        return response().withContentType(MediaType.APPLICATION_JSON)
                .withBody(jsonOf(resourceFileName));
    }

    @SneakyThrows
    public static String jsonOf(String name) {
        return IOUtils.resourceToString(name, Charset.defaultCharset(), TestUtils.class.getClassLoader());
    }
}
