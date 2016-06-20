/*
dump information about current map view (involved tiles, offset, geometric information..) as XML string

works with OpenLayers v2.x

prerequisites:
-map object with name 'map' exists and is globally accessible
-map.displayProjection is WGS84

this file can be included after the OpenLayers JavaScript files in the header section of a HTML file
<script src="map_view_info.js"></script>

calling localFileDownload() will create a (local) browser download request so that the created XML string can be saved to a (local) file

//tb/1606
*/

//=============================================================================
function localFileDownload(data, mimetype, filename)
{
	//http://stackoverflow.com/questions/2897619/using-html5-javascript-to-generate-and-save-a-file
	//data = [];
	//data.push("This is a test\n");

	//Specify the file's mime-type.
	//properties = {type: 'plain/text'};
	properties = {type: mimetype};

	try
	{
		//Specify the filename using the File constructor, but ...
		file = new File(data, filename, properties);
	}
	catch (e)
	{
		//... fall back to the Blob constructor if that isn't supported.
		file = new Blob(data, properties);
	}
	url = URL.createObjectURL(file);

	//uriContent = "data:application/octet-stream," + encodeURIComponent(content);

	//browser must allow 'pop-ups'
	dl_window = window.open(url, 'download');
}

//https://trac.osgeo.org/openlayers/wiki/TileStitchingPrinting
//=============================================================================
function getViewInfoXML()
{
	//calculate some view properties
	var map_bounds=map.calculateBounds();
	var map_bounds_wgs84=map_bounds.clone();
	map_bounds_wgs84.transform(map.getProjectionObject(),map.displayProjection);
//	console.log(map_bounds_wgs84);

	map_left_bottom=new OpenLayers.Geometry.Point(map_bounds_wgs84.left,map_bounds_wgs84.bottom);
	map_left_top=new OpenLayers.Geometry.Point(map_bounds_wgs84.left,map_bounds_wgs84.top);
	map_right_bottom=new OpenLayers.Geometry.Point(map_bounds_wgs84.right,map_bounds_wgs84.bottom);
	map_right_top=new OpenLayers.Geometry.Point(map_bounds_wgs84.right,map_bounds_wgs84.top);

	var line1= new OpenLayers.Geometry.LineString([map_left_top, map_right_top]);
	var line2= new OpenLayers.Geometry.LineString([map_left_bottom, map_right_bottom]);
	var line3= new OpenLayers.Geometry.LineString([map_left_top, map_left_bottom]);
	var line4= new OpenLayers.Geometry.LineString([map_right_top, map_right_bottom]);

	var dist1= line1.getGeodesicLength(map.displayProjection);
	var dist2= line2.getGeodesicLength(map.displayProjection);
	var dist3= line3.getGeodesicLength(map.displayProjection);
	var dist4= line4.getGeodesicLength(map.displayProjection);

	//tile offset in viewport
	var offsetX = parseInt(map.layerContainerDiv.style.left);
	var offsetY = parseInt(map.layerContainerDiv.style.top);
	var size = map.getSize();

	//write out view info as XML string with root element 'mapview'
	var tiles_xml = '<mapview>';

	//'view' contains generic info about the current map view
	tiles_xml+='<view width="'+size.w+'" height="'+size.h+'" zoom="'+map.getZoom()+'">';
	tiles_xml+='<center lon="'+map_bounds_wgs84.getCenterLonLat().lon.toFixed(6)+'" lat="'+map_bounds_wgs84.getCenterLonLat().lat.toFixed(6)+'"/>';
	tiles_xml+='<bounds left="'+map_bounds_wgs84.left.toFixed(6)+'" bottom="'+map_bounds_wgs84.bottom.toFixed(6)+'" right="'+map_bounds_wgs84.right.toFixed(6)+'" top="'+map_bounds_wgs84.top.toFixed(6)+'"/>';
	tiles_xml+='<edges left="'+dist3.toFixed(2)+'" bottom="'+dist1.toFixed(2)+'" right="'+dist1.toFixed(4)+'" top="'+dist2.toFixed(2)+'"/>';
	tiles_xml+='</view>';

	//go through all layers, and collect a list of objects
	//each object is a tile's URL and the tile's pixel location relative to the viewport
	for (layerindex in map.layers)
	{
		var layer = map.layers[layerindex];

		//if the layer isn't visible at this range, or is turned off, skip it
		if (!layer.getVisibility() || !layer.calculateInRange())
		{
			//create closed element 'layer' anyway, visible=false
			tiles_xml+='<layer index="'+layerindex+'" name="'+layer.name+'" visible="false"/>';
			continue;
		}

		//create open element 'layer' and therein 'tiles'
		tiles_xml+='<layer index="'+layerindex+'" name="'+layer.name+'" visible="true"><tiles>';

		//iterate through the grid's tiles, collecting each tile's extent and pixel location at this moment
		for (tilerow in layer.grid)
		{
			for (tilei in layer.grid[tilerow])
			{
				var tile	= layer.grid[tilerow][tilei]
				var url		= layer.getURL(tile.bounds);
				var position	= tile.position;
				var tilexpos	= position.x + offsetX;
				var tileypos	= position.y + offsetY;
				var opacity	= layer.opacity ? parseInt(100*layer.opacity) : 100;
				//create closed element 'tile'
				tiles_xml+='<tile url="'+url+'" row="'+tilerow+'" col="'+tilei+'" x="'+tilexpos+'" y="'+tileypos+'" opacity="'+opacity+'"/>';
			}
		}//end for each tilerow

		//close element 'tiles', 'layer'
		tiles_xml+='</tiles></layer>';
	}//end for each layer

	//close XML document
	tiles_xml+='</mapview>';

//	console.log(tiles_xml);
	return tiles_xml;
}//end getViewInfoXML

//=============================================================================
function downloadViewInfoXML(filename)
{
	data = [];
	data.push(getViewInfoXML());
	localFileDownload(data,'plain/text',filename);
}
//EOF
