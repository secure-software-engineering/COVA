
package cova.setup;

import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;

/**
 * The Class SootSetupForAndroid set soot configuration for android apk.
 * 
 */
public class SootSetupForAndroid {

  /**
   * Gets the soot configuration for android apk.
   *
   * @param writeJimpleOutput
   *          true, if prints jimple output
   * @return the soot configuration for android apk
   */
  public static SootConfigForAndroid getSootConfig() {
    return new SootConfigForAndroid() {
      @Override
      public void setSootOptions(Options options, InfoflowConfiguration config) {
        super.setSootOptions(options, config);// explicitly exclude packages for shorter runtime
        options.set_keep_line_number(true);
        options.set_print_tags_in_output(true);
      }
    };
  }

}
