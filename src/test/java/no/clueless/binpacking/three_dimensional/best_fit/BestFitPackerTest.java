package no.clueless.binpacking.three_dimensional.best_fit;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.clueless.binpacking.shared.NonEmptyList;
import no.clueless.binpacking.three_dimensional.shared.Item3D;
import no.clueless.binpacking.three_dimensional.shared.MaxLengthGirthPackingStrategy3D;
import no.clueless.binpacking.three_dimensional.shared.Size3D;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BestFitPackerTest {
    private final HttpClient   httpClient   = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void pack() throws IOException, InterruptedException {
        var sut = new BestFitPacker(new Size3D(300, 300, 300), List.of(new MaxLengthGirthPackingStrategy3D(240, 360)));
        var items = Stream.of(
                new Item3D(60, 1, 60),
                new Item3D(60, 1, 60),
                new Item3D(60, 1, 60),
                new Item3D(60, 1, 60),
                new Item3D(60, 1, 60),
                new Item3D(100, 10, 2),
                new Item3D(100, 10, 2),
                new Item3D(100, 10, 2),
                new Item3D(100, 10, 2),
                new Item3D(100, 100, 2),
                new Item3D(100, 100, 2),
                new Item3D(100, 100, 2),
                new Item3D(100, 100, 2),
                new Item3D(100, 100, 2),
                new Item3D(100, 100, 2),
                new Item3D(93, 40, 37),
                new Item3D(120, 90, 30)
        ).collect(NonEmptyList.collector());

        // act
        var actual = sut.pack(items).orElse(null);

        // assert
        assertNotNull(actual);
        var payload = objectMapper.writeValueAsString(actual);
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/broadcast"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        System.out.println(actual);
    }
}