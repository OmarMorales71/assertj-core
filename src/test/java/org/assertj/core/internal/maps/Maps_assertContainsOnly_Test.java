/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2020 the original author or authors.
 */
package org.assertj.core.internal.maps;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.data.MapEntry.entry;
import static org.assertj.core.error.ShouldContainOnly.shouldContainOnly;
import static org.assertj.core.internal.ErrorMessages.entriesToLookForIsEmpty;
import static org.assertj.core.internal.ErrorMessages.entriesToLookForIsNull;
import static org.assertj.core.test.TestData.someInfo;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.AssertionsUtil.assertThatAssertionErrorIsThrownBy;
import static org.assertj.core.util.AssertionsUtil.expectAssertionError;
import static org.assertj.core.util.FailureMessages.actualIsNull;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Map;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.data.MapEntry;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Maps;
import org.junit.jupiter.api.Test;

/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Maps#assertContainsOnly(org.assertj.core.api.AssertionInfo, java.util.Map, org.assertj.core.data.MapEntry...)}</code>
 * .
 *
 * @author Jean-Christophe Gay
 */
class Maps_assertContainsOnly_Test extends MapsBaseTest {

  @SuppressWarnings("unchecked")
  @Test
  void should_fail_if_actual_is_null() {
    // GIVEN
    actual = null;
    // WHEN
    ThrowingCallable code = () -> maps.assertContainsOnly(someInfo(), actual, entry("name", "Yoda"));
    // THEN
    assertThatAssertionErrorIsThrownBy(code).withMessage(actualIsNull());
  }

  @SuppressWarnings("unchecked")
  @Test
  void should_fail_if_given_entries_array_is_null() {
    assertThatNullPointerException().isThrownBy(() -> maps.assertContainsOnly(someInfo(), actual, (MapEntry[]) null))
                                    .withMessage(entriesToLookForIsNull());
  }

  @SuppressWarnings("unchecked")
  @Test
  void should_fail_if_given_entries_array_is_empty() {
    assertThatIllegalArgumentException().isThrownBy(() -> maps.assertContainsOnly(someInfo(), actual, emptyEntries()))
                                        .withMessage(entriesToLookForIsEmpty());
  }

  @SuppressWarnings("unchecked")
  @Test
  void should_pass_if_actual_and_entries_are_empty() {
    maps.assertContainsOnly(someInfo(), emptyMap(), emptyEntries());
  }

  @SuppressWarnings("unchecked")
  @Test
  void should_pass_if_actual_contains_only_expected_entries() {
    maps.assertContainsOnly(someInfo(), actual, entry("name", "Yoda"), entry("color", "green"));
  }

  @Test
  void should_fail_if_actual_contains_unexpected_entry() {
    // GIVEN
    AssertionInfo info = someInfo();
    MapEntry<String, String>[] expected = array(entry("name", "Yoda"));
    // WHEN
    expectAssertionError(() -> maps.assertContainsOnly(info, actual, expected));
    // THEN
    verify(failures).failure(info, shouldContainOnly(actual, expected, emptySet(), set(entry("color", "green"))));
  }

  @Test
  void should_fail_if_actual_does_not_contains_every_expected_entries() {
    // GIVEN
    AssertionInfo info = someInfo();
    MapEntry<String, String>[] expected = array(entry("name", "Yoda"), entry("color", "green"));
    Map<String, String> underTest = Maps.mapOf(entry("name", "Yoda"));
    // WHEN
    expectAssertionError(() -> maps.assertContainsOnly(info, underTest, expected));
    // THEN
    verify(failures).failure(info, shouldContainOnly(underTest, expected, set(entry("color", "green")), emptySet()));
  }

  @Test
  void should_fail_if_actual_does_not_contains_every_expected_entries_and_contains_unexpected_one() {
    // GIVEN
    AssertionInfo info = someInfo();
    MapEntry<String, String>[] expected = array(entry("name", "Yoda"), entry("color", "green"));
    Map<String, String> underTest = Maps.mapOf(entry("name", "Yoda"), entry("job", "Jedi"));
    // WHEN
    expectAssertionError(() -> maps.assertContainsOnly(info, underTest, expected));
    // THEN
    verify(failures).failure(info,
                             shouldContainOnly(underTest, expected, set(entry("color", "green")), set(entry("job", "Jedi"))));
  }

  @Test
  void should_fail_if_actual_contains_entry_key_with_different_value() {
    // GIVEN
    AssertionInfo info = someInfo();
    MapEntry<String, String>[] expectedEntries = array(entry("name", "Yoda"), entry("color", "yellow"));
    // WHEN
    expectAssertionError(() -> maps.assertContainsOnly(info, actual, expectedEntries));
    // THEN
    verify(failures).failure(info, shouldContainOnly(actual, expectedEntries, set(entry("color", "yellow")),
                                                     set(entry("color", "green"))));
  }

  private static <K, V> HashSet<MapEntry<K, V>> set(MapEntry<K, V> entry) {
    HashSet<MapEntry<K, V>> set = new HashSet<>();
    set.add(entry);
    return set;
  }

}
