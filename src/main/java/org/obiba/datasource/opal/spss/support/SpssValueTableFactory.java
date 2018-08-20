/*
 * Copyright (c) 2017 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.datasource.opal.spss.support;

import com.google.common.base.Strings;
import org.obiba.datasource.opal.spss.SpssValueTable;
import org.obiba.magma.Datasource;
import org.obiba.magma.support.DatasourceParsingException;
import org.opendatafoundation.data.spss.SPSSFile;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SpssValueTableFactory {

  @NotNull
  private final Datasource datasource;

  @NotNull
  private final String entityType;

  @NotNull
  private final File file;

  @NotNull
  private final String characterSet;

  private final String locale;

  private final String name;

  private final String idVariable;

  public SpssValueTableFactory(@NotNull Datasource datasource, @NotNull String entityType, @NotNull File file,
                               @NotNull String characterSet, @NotNull String locale, @Nullable String idVariable) {
    this.datasource = datasource;
    this.entityType = entityType;
    this.file = file;
    this.characterSet = characterSet;
    this.locale = locale;
    this.idVariable = idVariable;
    name = createValidFileName(file);
  }

  public SpssValueTable create() {
    try {
      SPSSFile spssFile = new SPSSFile(file,
          Strings.isNullOrEmpty(characterSet) ? null : Charset.forName(characterSet));
      spssFile.logFlag = false;

      return new SpssValueTable(datasource, name, entityType, locale, idVariable, spssFile);
    } catch(IOException e) {
      String fileName = file.getName();
      throw new DatasourceParsingException("Could not open file " + fileName + " to create ValueTable.", e,
          "FailedToOpenFile", fileName);
    }
  }

  public String getName() {
    return name;
  }

  private String createValidFileName(File sourceFile) {
    String filename = sourceFile.getName();
    int postion = filename.lastIndexOf('.');

    if(postion > 0) {
      filename = filename.substring(0, postion);
    }

    return filename.replaceAll("[.:? ]", "");
  }

}
