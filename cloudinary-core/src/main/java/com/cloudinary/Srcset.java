package com.cloudinary;

import com.cloudinary.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to configure srcset and sizes attributes for image tags. Used with {@link TagOptions#srcset(Srcset)}
 * when calling {@link Url#imageTag(String, TagOptions)}, or directly using the Cloudinary tag lib library.
 */
public class Srcset {
    private final int[] breakpoints;
    private boolean sizes = false;

    private Srcset(int[] breakpoints) {
        this.breakpoints = breakpoints;
    }

    /**
     * Get an instance configured for specific width
     * @param breakpoints An array of integers specifying the required widths, in pixels.
     * @return The resulting Srcset instance to send to {@link TagOptions#srcset}
     */
    public static Srcset breakpoints(int[] breakpoints) {
        Arrays.sort(breakpoints);
        return new Srcset(breakpoints);
    }

    /**
     * Get an instance configured for widths covering the requests range
     * @param minWidth The width of the smallest image in pixels
     * @param maxWidth The width of the largest image in pixels
     * @param maxImages The total count of generated images.
     * @return The resulting Srcset instance to send to {@link TagOptions#srcset}
     */
    public static Srcset breakpoints(int minWidth, int maxWidth, int maxImages) {
        //max_images - 1 if max_images > 1 else 1)
        int stepSize = (int) Math.round(Math.ceil((float) (maxWidth - minWidth)) /
                (maxImages > 1 ? maxImages - 1 : maxImages));
        int curr = minWidth;
        int[] breakpoints = new int[maxImages];
        for (int i = 0; i < maxImages; i++) {
            breakpoints[i] = curr;
            curr += stepSize;
        }

        return new Srcset(breakpoints);
    }

    /**
     * Set the sizes param to `true` to generate a sizes attribute.
     * @param sizes
     * @return
     */
    public Srcset sizes(boolean sizes) {
        this.sizes = sizes;
        return this;
    }

    SrcsetResult generateSrcset(String source, Url url) {
        if (breakpoints == null || breakpoints.length < 1) {
            // TODO exception?
            return null;
        }

        Url current = url.clone();

        final String baseTransform = current.transformation != null ? current.transformation.generate() + "/" : "";
        List<String> srcsetItems = new ArrayList<>(breakpoints.length);

        String generatedUrl = null;
        for (int breakpoint : breakpoints) {
            current = current.clone();
            current.transformation(new Transformation().rawTransformation(baseTransform + "/c_scale,w_" + breakpoint));
            generatedUrl = current.generate(source);
            srcsetItems.add( generatedUrl + " " + breakpoint + "w");
        }

        // last generated url is the largest
        return new SrcsetResult(StringUtils.join(srcsetItems, ", "), generatedUrl);
    }

    String generateSizes() {
        String format = "(max-width: %dpx) %dpx";
        List<String> sizes = new ArrayList<>(breakpoints.length);

        for (int breakpoint : breakpoints) {
            sizes.add(String.format(format, breakpoint, breakpoint));
        }

        return StringUtils.join(sizes, ", ");
    }

    boolean hasSizes() {
        return sizes;
    }

    /**
     * Hold the results of the srcset generation
     */
    static final class SrcsetResult{
        final String srcset;
        final String largestBreakpoint;

        SrcsetResult(String srcset, String largestBreakpointUrl) {
            this.srcset = srcset;
            this.largestBreakpoint = largestBreakpointUrl;
        }
    }
}
