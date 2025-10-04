/* Preprocessed source code */
package haven.res.ui.tt.attrmod;

import haven.*;
import static haven.PUtils.*;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

@Resource.PublishedCode(name = "attrmod")
@FromResource(name = "ui/tt/attrmod", version = 12)
public class inormattr extends resattr {
    public final int dec;

    public inormattr(Resource res, Object... args) {
	super(res);
	dec = (args.length > 0) ? Utils.iv(args[0]) : 1;
    }

    public String format(double val) {
	double delta = (1.0 / (1.0 + val)) - 1.0;
	String bval = (Math.abs(delta) >= 10) ?
	    String.format("%s\u00d7", Utils.odformat2(Math.abs(delta), dec)) :
	    String.format("%s%%", Utils.odformat2(Math.abs(delta) * 100, dec));
	return(String.format("%s{%s%s}",
			     RichText.Parser.col2a((delta < 0) ? buff : debuff),
			     (delta < 0) ? "-" : "+", bval));
    }
}
