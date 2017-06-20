/*
Author: Nate Sutton
Copyright: 2017

This is code which captures the current display image to
reformat it in left and right eye image streams.  A
buffered image capture is planned to be streamed.

References:
Implemented code based on
https://stackoverflow.com/questions/8249669/how-do-take-a-screenshot-correctly-with-xlib
*/

#include <X11/Xlib.h>
#include <X11/X.h>

#include <cstdio>
#include "CImg.h"

using namespace cimg_library;

int main()
{
   Display *display = XOpenDisplay(NULL);
   Window root = DefaultRootWindow(display);

   XWindowAttributes gwa;

   XGetWindowAttributes(display, root, &gwa);
   int width = gwa.width;
   int height = gwa.height;


   XImage *image = XGetImage(display,root, 0,0 , width,height,AllPlanes, ZPixmap);

   unsigned char *array = new unsigned char[width * height * 3];

   unsigned long red_mask = image->red_mask;
   unsigned long green_mask = image->green_mask;
   unsigned long blue_mask = image->blue_mask;

   CImg<unsigned char> pic(array,width,height,1,3);

   for (int x = 0; x < width; x++)
      for (int y = 0; y < height ; y++)
      {
         unsigned long pixel = XGetPixel(image,x,y);

         unsigned char blue = pixel & blue_mask;
         unsigned char green = (pixel & green_mask) >> 8;
         unsigned char red = (pixel & red_mask) >> 16;

         array[(x + width * y) * 3] = red;
         array[(x + width * y) * 3+1] = green;
         array[(x + width * y) * 3+2] = blue;

         pic(x,y,0) = red;
         pic(x,y,1) = green;
         pic(x,y,2) = blue;
      }

   //CImg<unsigned char> pic(array,width,height,1,3);
   pic.save_png("blah.png");

   printf("%ld %ld %ld\n",red_mask>> 16, green_mask>>8, blue_mask);

    Display                 *display2;
    Visual                  *visual;
    int                     depth;
    int                     text_x;
    int                     text_y;
    XSetWindowAttributes    frame_attributes;
    Window                  frame_window;
    XFontStruct             *fontinfo;
    XGCValues               gr_values;
    GC                      graphical_context;
    XKeyEvent               event;
    char                    hello_string[] = "Hello World";
    int                     hello_string_length = strlen(hello_string);

    display2 = XOpenDisplay(NULL);
    visual = DefaultVisual(display2, 0);
    depth  = DefaultDepth(display2, 0);

    frame_attributes.background_pixel = XWhitePixel(display2, 0);

    /* create the application window */
    frame_window = XCreateWindow(display2, XRootWindow(display2, 0),
                                 0, 0, 400, 400, 5, depth,
                                 InputOutput, visual, CWBackPixel,
                                 &frame_attributes);
    XStoreName(display2, frame_window, "Hello World Example");

    XSelectInput(display2, frame_window, ExposureMask | StructureNotifyMask);
    fontinfo = XLoadQueryFont(display2, "10x20");
    gr_values.font = fontinfo->fid;
    gr_values.foreground = XBlackPixel(display2, 0);
    graphical_context = XCreateGC(display2, frame_window,
                                  GCFont+GCForeground, &gr_values);
    XMapWindow(display2, frame_window);

    while ( 1 ) {
        XNextEvent(display2, (XEvent *)&event);
        switch ( event.type ) {
            case Expose:
            {
                XWindowAttributes window_attributes;
                int font_direction, font_ascent, font_descent;
                XCharStruct text_structure;
                XTextExtents(fontinfo, hello_string, hello_string_length,
                             &font_direction, &font_ascent, &font_descent,
                             &text_structure);
                XGetWindowAttributes(display2, frame_window, &window_attributes);
                text_x = (window_attributes.width - text_structure.width)/2;
                text_y = (window_attributes.height -
                          (text_structure.ascent+text_structure.descent))/2;
                XDrawString(display2, frame_window, graphical_context,
                            text_x, text_y, hello_string, hello_string_length);
                break;
            }
            default:
                break;
        }
    }

   return 0;
}
