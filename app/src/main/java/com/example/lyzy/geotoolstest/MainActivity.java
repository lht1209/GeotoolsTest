package com.example.lyzy.geotoolstest;


import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vividsolutions.jts.awt.PointShapeFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity
{
    String testPath = Environment.getExternalStorageDirectory().getPath() + "/ceshi/test.shp";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void write(String filepath) {
        try {
            //创建shape文件对象
            File file = new File(filepath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            //定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            tb.add("the_geom", PointShapeFactory.Point.class);
            tb.add("POIID", Long.class);
            tb.add("NAMEC", String.class);
            ds.createSchema(tb.buildFeatureType());
            ds.setCharset(Charset.forName("GBK"));

            //设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            //写下一条
            SimpleFeature feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.123, 39.345)));
            feature.setAttribute("POIID", 1234567890l);
            feature.setAttribute("NAMEC", "某兴趣点1");
            feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.456, 39.678)));
            feature.setAttribute("POIID", 1234567891l);
            feature.setAttribute("NAMEC", "某兴趣点2");
            writer.write();
            writer.close();
            ds.dispose();

            //读取刚写完shape文件的图形信息
            ShpFiles shpFiles = new ShpFiles(filepath);
            ShapefileReader reader = new ShapefileReader(shpFiles, false, true, new GeometryFactory(), false);
            try {
                while (reader.hasNext()) {
                    System.out.println(reader.nextRecord().shape());
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) { }
    }

    public void btntest(View view) {
        write(testPath);
    }
}
