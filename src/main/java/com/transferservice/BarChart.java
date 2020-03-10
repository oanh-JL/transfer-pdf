package com.transferservice;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;

import java.io.FileOutputStream;

public class BarChart {

    public static void main(String[] args) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");

        Row row;
        Cell cell;

        row = sheet.createRow(0);
        row.createCell(0);
        row.createCell(1).setCellValue("HEADER 1");
        row.createCell(2).setCellValue("HEADER 2");
        row.createCell(3).setCellValue("HEADER 3");

        for (int r = 1; r < 5; r++) {
            row = sheet.createRow(r);
            cell = row.createCell(0);
            cell.setCellValue("Serie " + r);
            cell = row.createCell(1);
            cell.setCellValue(new java.util.Random().nextDouble());
            cell = row.createCell(2);
            cell.setCellValue(new java.util.Random().nextDouble());
            cell = row.createCell(3);
            cell.setCellValue(new java.util.Random().nextDouble());
        }

        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 8, 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("bar chart");
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("x");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("f(x)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, 3));
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, 3));
        XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, 3));
        XDDFNumericalDataSource<Double> ys3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(3, 3, 0, 3));
        XDDFNumericalDataSource<Double> ys4 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(4, 4, 0, 3));

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(xs, ys1);
        series1.setTitle("series1", null);
        XDDFChartData.Series series2 = data.addSeries(xs, ys2);
        series2.setTitle("series2", null);
        XDDFChartData.Series series3 = data.addSeries(xs, ys3);
        series2.setTitle("series3", null);
        XDDFChartData.Series series4 = data.addSeries(xs, ys4);
        series2.setTitle("series4", null);
        chart.plot(data);

        CTChart ctChart = ((XSSFChart)chart).getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
        CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
        ctBoolean.setVal(true);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);

        solidFillSeries(data, 0, PresetColor.CHARTREUSE);
        solidFillSeries(data, 1, PresetColor.TURQUOISE);
        solidFillSeries(data, 2, PresetColor.AQUA);
        solidFillSeries(data, 3, PresetColor.CHOCOLATE);

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("u-bar-chart.xlsx")) {
            wb.write(fileOut);
        }

//        for (int r = 2; r < 6; r++) {
//            CTBarSer ctBarSer = ctBarChart.addNewSer();
//            CTSerTx ctSerTx = ctBarSer.addNewTx();
//            CTStrRef ctStrRef = ctSerTx.addNewStrRef();
//            ctStrRef.setF("Sheet1!$A$" + r);
//            ctBarSer.addNewIdx().setVal(r-2);
//            CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
//            ctStrRef = cttAxDataSource.addNewStrRef();
//            ctStrRef.setF("Sheet1!$B$1:$D$1");
//            CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
//            CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
//            ctNumRef.setF("Sheet1!$B$" + r + ":$D$" + r);
//
//            //at least the border lines in Libreoffice Calc ;-)
//            ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] {0,0,0});
//
//        }
//
//        //telling the BarChart that it has axes and giving them Ids
//        ctBarChart.addNewAxId().setVal(123456);
//        ctBarChart.addNewAxId().setVal(123457);
//
//        //cat axis
//        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
//        ctCatAx.addNewAxId().setVal(123456); //id of the cat axis
//        CTScaling ctScaling = ctCatAx.addNewScaling();
//        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
//        ctCatAx.addNewDelete().setVal(false);
//        ctCatAx.addNewAxPos().setVal(STAxPos.B);
//        ctCatAx.addNewCrossAx().setVal(123457); //id of the val axis
//        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
//
//        //val axis
//        CTValAx ctValAx = ctPlotArea.addNewValAx();
//        ctValAx.addNewAxId().setVal(123457); //id of the val axis
//        ctScaling = ctValAx.addNewScaling();
//        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
//        ctValAx.addNewDelete().setVal(false);
//        ctValAx.addNewAxPos().setVal(STAxPos.L);
//        ctValAx.addNewCrossAx().setVal(123456); //id of the cat axis
//        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
//
//        //legend
//        CTLegend ctLegend = ctChart.addNewLegend();
//        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
//        ctLegend.addNewOverlay().setVal(false);
//
//        System.out.println(ctChart);
//
//        FileOutputStream fileOut = new FileOutputStream("oanh-BarChart.xlsx");
//        wb.write(fileOut);
//        fileOut.close();
    }
    private static void solidFillSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        series.setShapeProperties(properties);
    }
}
