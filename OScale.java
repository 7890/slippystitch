//create SVG scale

//tb/160713

//javac OScale.java
//(cat a.svg | grep -v '</svg>'; java OScale; echo '</svg>') | xmlstarlet fo > appended.svg
//echo "appended.svg --export-area-page --export-text-to-path --export-plain-svg=/tmp/out.svg "|inkscape --shell
//echo "appended.svg --export-area-page --export-background="#ffffff" --export-background-opacity="1.0" --export-png=appended.png "|inkscape --shell

//http://tavmjong.free.fr/INKSCAPE/MANUAL/html/CommandLine.html
/*
-e, --export-png

    Export a PNG file. 
-P, --export-ps

    Export a PS file. 
-E, --export-eps

    Export an EPS file. 
-A, --export-pdf

    Export a PDF file. 
-l, --export-plain-svg

Export a plain SVG file. A plain SVG has all the Inkscape-specific information and the RFD metadata removed. 
A program that displays SVG files should ignore all such information according to the SVG specification, 
so this option should not in principle be necessary. 

-C, --export-area-page

    The area exported will correspond to the area defined by the page. 


-T, --export-text-to-path

The text objects should be converted to paths prior to export to a PS or EPS export. 
Then ensures that the text will be rendered properly regardless of which fonts are installed on a 
computer that displays or a printer that prints the resulting file. 

*/

//=============================================================================
//=============================================================================
class OScale
{
	//landscape
	public int scale_pixels_max=700;

	//zoom 11
	public float meter_per_pixel=51.637180f;
	//public float pixel_per_meter=0.019366f;
	public float pixel_per_meter=1f/meter_per_pixel;

	public int scale_pixels_min=5;

	public int scale_offset_x=20;
	public int scale_offset_y=20;

	public int scale_rect_width=40;

	public int scale_tick_increment=10;

	public int cross_offset_x=80;
	public int cross_offset_y=20;

	public int cross_edge_length=60;

	public int fontsize=14;

	//start with 1 meter
	public float smallest_value=(float)Math.pow(10,0);

	//count of scales
	public int count_of_scales=7;

	//array to hold meter scales
	private float tenBase[];

	private int index_smallest=-1;
	private int index_largest=-1;

	private float scale_total_m=0;

//=============================================================================
	public static void main(String[] args) throws Exception
	{
		OScale o=new OScale(args);
	}

//=============================================================================
	public OScale(String[] args) throws Exception
	{
		init(args);
		printScale();
		printWindRose();
	}

//=============================================================================
	private void init(String[] args) throws Exception
	{
		///load properties
		///help

		//parse args
		if(args.length>0)
		{
			meter_per_pixel=Float.parseFloat(args[0]);
			pixel_per_meter=1f/meter_per_pixel;
		}
		if(args.length>1)
		{
			scale_pixels_max=Integer.parseInt(args[1]);
		}

		scale_total_m=scale_pixels_max * meter_per_pixel;

		tenBase=new float[count_of_scales];

		for(int i=0;i<tenBase.length;i++)
		{
			tenBase[i]=(float)(smallest_value*Math.pow(10,i));

			float scale_in_pixels=tenBase[i]*pixel_per_meter;

			if(scale_in_pixels<scale_pixels_min)
			{
//				System.out.println("TOO SMALL "+tenBase[i]+" "+scale_in_pixels);
				continue;
			}

			if(scale_in_pixels<=scale_pixels_max)
			{
				if(index_smallest<0)
				{
					index_smallest=i;
				}
//				System.out.println(""+tenBase[i]+" "+scale_in_pixels);
				index_largest=i;
			}
		}
	}//end init()

//=============================================================================
	public void printScale()
	{
		int deep=index_largest-index_smallest+1;

		//group using offset to top left of page
		System.out.println("<g id=\"g_scale\" transform=\"translate("+scale_offset_x+","+scale_offset_y+")\">");

		//background rectangle for text, slightly transparent
		System.out.println("<rect style=\"fill:#ffffff;opacity:0.85;stroke:none;\""
			+" id=\"rect_scale_text\""
			+" width=\""+100+"\""
			+" height=\""+fontsize+"\""
			+" x=\""+scale_rect_width+"\""
			+" y=\""+(scale_pixels_max-fontsize)+"\""
			+" ry=\"0\" />");

		//background for scale rectangle, slightly transparent
		System.out.println("<rect style=\"fill:#ffffff;opacity:0.85;stroke:#555555;stroke-width:1;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;stroke-dashoffset:0\""
			+" id=\"rect_scale\""
			+" width=\""+scale_rect_width+"\""
			+" height=\""+scale_pixels_max+"\""
			+" x=\"0\""
			+" y=\"0\""
			+" ry=\"0\" />");

		//for every scale
		for(int i=0;i<deep;i++)
		{
			float pixels=tenBase[i+index_smallest]*pixel_per_meter;
//			System.out.println(""+i+" "+Math.pow(10,i+index_smallest)+" "+pixels);

			//draw ticks for current scale until before max
			for(float k=0;k<scale_pixels_max;k+=pixels)
			{
				//draw tick. larger scales are wider
				System.out.println("<path id=\""+k+"\" style=\"fill:none;stroke:#000000;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\""
					+" d=\"M0,"+k+" L"+((i+1)*scale_tick_increment)+","+k+"\""
					+"/>"
				);
			}
		}
/*
		//'0 m'
		System.out.println("<flowRoot xml:space=\"preserve\" "
			+ "style=\"font-style:normal;font-weight:normal;font-size:"+fontsize+"px;line-height:125%;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\">"
			+ "<flowRegion><rect width=\"100\" height=\"25\" x=\""+(scale_rect_width+3)+"\" y=\""+0+"\"/></flowRegion>"
			+ "<flowPara id=\"flowPara_cross_north\">0 m</flowPara>"
			+ "</flowRoot>"
			+"/>"
		);
*/
		//'fff.ff m'
		System.out.println("<flowRoot xml:space=\"preserve\" "
			+ "style=\"font-style:normal;font-weight:normal;font-size:"+fontsize+"px;line-height:125%;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\">"
			+ "<flowRegion><rect width=\"100\" height=\"25\" x=\""+(scale_rect_width+3)+"\" y=\""+(scale_pixels_max-fontsize)+"\"/></flowRegion>"
			+ "<flowPara id=\"flowPara_cross_north\">"+formatDistance(scale_total_m)+"</flowPara>"
			+ "</flowRoot>"
			+"/>"
		);

		System.out.println("</g>");
	}//end printScale()

//=============================================================================
	public void printWindRose()
	{
		//in group
		System.out.println("<g id=\"g_wind_rose\" transform=\"translate("+cross_offset_x+","+cross_offset_y+")\">");

		//background rectangle for cross, slightly transparent
		System.out.println("<rect style=\"fill:#ffffff;opacity:0.85;stroke:none;\""
			+" id=\"rect_wind_rose\""
			+" width=\""+(cross_edge_length)+"\""
			+" height=\""+(cross_edge_length)+"\""
			+" x=\""+0+"\""
			+" y=\""+0+"\""
			+" ry=\"0\" />");


		System.out.println("<path id=\"path_cross_horizontal\" style=\"fill:none;stroke:#000000;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\""
			+" d=\"M0,"+(cross_edge_length/2)+" L"+cross_edge_length+","+(cross_edge_length/2)+"\""
			+"/>"
		);
		System.out.println("<path id=\"path_cross_vertical\" style=\"fill:none;stroke:#000000;stroke-width:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\""
			+" d=\"M"+(cross_edge_length/2)+",0 L"+(cross_edge_length/2)+","+cross_edge_length+"\""
			+"/>"
		);

		//'N'
		System.out.println("<flowRoot xml:space=\"preserve\" "
			+ "style=\"font-style:normal;font-weight:normal;font-size:"+fontsize+"px;line-height:125%;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\">"
			+ "<flowRegion><rect width=\"25\" height=\"25\" x=\""+((cross_edge_length/2)+3)+"\" y=\"0\"/></flowRegion>"
			+ "<flowPara id=\"flowPara_cross_north\">N</flowPara>"
			+ "</flowRoot>"
			+"/>"
		);
		System.out.println("</g>");
	}//end printWindRose()

//=============================================================================
	public String formatDistance(float meter)
	{
		if(meter < 1000)
		{
			return String.format("%.1f m", meter);
		}
		else
		{
			return String.format("%.1f km", meter/1000);
		}
	}
}//end class OScale
//EOF
