package com.bsi.pusbin.modules.grafik.schema;

import java.util.List;

public record ChartResponse(List<String> x, List<?> y) {}
