#!/bin/bash
cd data/Raster_geotiff/
rm ../Raster/* -f
for filename in *; do
    echo $filename
    # If you havent remade the image since I updated the dockerfile, this command can be insalled with:
    # apt update
    # apt-get install gdal-bin -y
    gdal_translate -of GTiff -co "PROFILE=BASELINE" $filename ../Raster/$filename
done
rm ../Raster/*.tif.aux.xml
cd ../..
echo Done Converting Geotifs to tifs