# slippystitch
Linux shell script to create map image from OpenLayers v2.x slippy map view


```
$ ./slippystitch testdata/map_view.xml | display -
processing layer OpenStreetMap
writing output to /tmp/tmp.TGSOlQ66MD/OpenStreetMap.png
temporary working dir: /tmp/tmp.uhrlkI7dy4
parsing data from testdata/map_view.xml
creating directories for tiles
creating download commands
downloading tiles..........................
grouping cols
creating commands for montage (cols)
executing tile montage (cols)
executing tile montage (append cols)
cropping image to view 800x1000+67+65
removing temporary working dir: /tmp/tmp.uhrlkI7dy4
layer done!

processing layer toner-lines
writing output to /tmp/tmp.TGSOlQ66MD/toner-lines.png
temporary working dir: /tmp/tmp.PHqoDZD52Y
parsing data from testdata/map_view.xml
creating directories for tiles
creating download commands
downloading tiles..........................
grouping cols
creating commands for montage (cols)
executing tile montage (cols)
executing tile montage (append cols)
cropping image to view 800x1000+67+65
removing temporary working dir: /tmp/tmp.PHqoDZD52Y
layer done!

all done.

```
