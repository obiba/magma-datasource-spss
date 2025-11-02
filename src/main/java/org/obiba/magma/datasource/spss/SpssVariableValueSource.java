/*
 * Copyright (c) 2017 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.magma.datasource.spss;

import org.obiba.magma.datasource.spss.support.SpssVariableValueFactory;
import org.obiba.magma.*;
import org.opendatafoundation.data.spss.SPSSVariable;

import javax.validation.constraints.NotNull;
import java.util.*;

public class SpssVariableValueSource extends AbstractVariableValueSource implements VariableValueSource, VectorSource {

  private final Variable variable;

  private final SPSSVariable spssVariable;

  private final Map<String, List<Integer>> identifierToVariableIndex;

  public SpssVariableValueSource(Variable variable, SPSSVariable spssVariable,
                                 Map<String, List<Integer>> identifierToVariableIndex) {
    this.variable = variable;
    this.spssVariable = spssVariable;
    this.identifierToVariableIndex = identifierToVariableIndex;
  }

  @NotNull
  @Override
  public Variable getVariable() {
    return variable;
  }

  @NotNull
  @Override
  public ValueType getValueType() {
    return variable.getValueType();
  }

  @Override
  public Iterable<Value> getValues(final Iterable<VariableEntity> entities) {
    return () -> new ValuesIterator(entities);
  }

  @NotNull
  @Override
  public Value getValue(ValueSet valueSet) {
    ValueSet vs = valueSet;
    if (valueSet instanceof ValueSetWrapper) {
      vs = ((ValueSetWrapper) valueSet).getWrapped();
    }
    SpssValueSet spssValueSet = (SpssValueSet) vs;
    return spssValueSet.getValue(variable);
  }

  @Override
  public boolean supportVectorSource() {
    return true;
  }

  @NotNull
  @Override
  public VectorSource asVectorSource() {
    return this;
  }

  //
  // Inner classes
  //

  private class ValuesIterator implements Iterator<Value> {

    private final Iterator<VariableEntity> entitiesIterator;

    private ValuesIterator(Iterable<VariableEntity> entities) {
      entitiesIterator = entities.iterator();
    }

    @Override
    public boolean hasNext() {
      return entitiesIterator.hasNext();
    }

    @Override
    public Value next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      VariableEntity variableEntity = entitiesIterator.next();
      List<Integer> valuesIndex = identifierToVariableIndex.get(variableEntity.getIdentifier());
      return new SpssVariableValueFactory(valuesIndex, spssVariable, variable.getValueType(), false, variable.isRepeatable())
          .create();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}
