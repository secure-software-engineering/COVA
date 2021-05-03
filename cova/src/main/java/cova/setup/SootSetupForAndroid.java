/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package cova.setup;

import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;

/** The Class SootSetupForAndroid set soot configuration for android apk. */
public class SootSetupForAndroid {

  /**
   * Gets the soot configuration for android apk.
   *
   * @param writeJimpleOutput true, if prints jimple output
   * @return the soot configuration for android apk
   */
  public static SootConfigForAndroid getSootConfig() {
    return new SootConfigForAndroid() {
      @Override
      public void setSootOptions(Options options, InfoflowConfiguration config) {
        super.setSootOptions(options, config); // explicitly exclude packages for shorter runtime
        options.set_keep_line_number(true);
        options.set_print_tags_in_output(true);
        options.set_process_multiple_dex(true);
      }
    };
  }
}
