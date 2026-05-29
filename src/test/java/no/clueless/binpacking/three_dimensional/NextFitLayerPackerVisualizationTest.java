package no.clueless.binpacking.three_dimensional;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.clueless.binpacking.shared.NonEmptyList;
import no.clueless.binpacking.three_dimensional.shared.Bin3D;
import no.clueless.binpacking.three_dimensional.shared.Size3D;
import no.clueless.binpacking.visualization.JettyVisualizer;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Stream;

public class NextFitLayerPackerVisualizationTest {
    @Test
    void visualize() throws JsonProcessingException {
        var bins = Stream.of(new Bin3D(UUID.randomUUID().toString(), new Size3D(10, 20, 30))).collect(NonEmptyList.collector());
        JettyVisualizer.preview(bins);
    }
}
