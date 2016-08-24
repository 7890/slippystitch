#!/bin/sh

#//tb/1607

###needs tool check

#========================================================================
if [ $# -lt 1 ]
then
	echo "create_path_from_geometry.sh: need argument: XML file">&2

	echo "">&2
	echo "***help***">&2
	echo "create_path_from_geometry.sh creates svg path from feature geometries found in the XML file">&2
	exit 1
fi

#first arg: xml file
view_xml="$1"

stroke_color=000000
stroke_width=5
stroke_opacity=0.8

style="fill:none;stroke:#"$stroke_color";stroke-width:"$stroke_width";stroke-linecap:round;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:"$stroke_opacity";stroke-dasharray:none"

#==============================================================================
svg_path()
{
	layername="$1"

	(
		echo "<g transform=\"translate(0,0)\">";
		cat "$view_xml" | xmlstarlet sel -t -m "//layer[@name='${layername}']/features/feature" -e path -a id -v @id -b \
			-a style -o "$style" -b \
			-a d \
			-m "geometry/point[1]" -o 'M' -v @x -o "," -v @y -o " " -b \
			-m "geometry/point[position()>1]" -o 'L' -v @x -o "," -v @y -o " " -b \
			-b \
			-b -n -b -n;
		echo "</g>"
	) | xmlstarlet fo --omit-decl 
}

#==============================================================================
#clipPath_page: defined in defs (created by svgimage)
echo '<g id="group_features_geometry" clip-path="url(#clipPath_page)" transform="translate(0,0)">'

#get list of every visible layer having features
cat ~/download/map_view.xml | xmlstarlet sel -t -m "//layer[@visible='true']/features/feature" -v ../../@name -n \
	| while read line; do svg_path "$line"; done

echo '</g>'

exit
