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
package org.assertj.core.internal.paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.error.ShouldBeDirectory.shouldBeDirectory;
import static org.assertj.core.error.ShouldExist.shouldExist;
import static org.assertj.core.error.ShouldNotBeEmpty.shouldNotBeEmpty;
import static org.assertj.core.util.AssertionsUtil.expectAssertionError;
import static org.assertj.core.util.FailureMessages.actualIsNull;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.Paths;
import org.junit.jupiter.api.Test;

/**
 * Tests for <code>{@link Paths#assertIsNotEmptyDirectory(AssertionInfo, Path)}</code>
 *
 * @author Valeriy Vyrva
 */
class Paths_assertIsNotEmptyDirectory_Test extends MockPathsBaseTest {

  @Test
  void should_pass_if_actual_is_not_empty() {
    // GIVEN
    List<Path> files = list(mockEmptyRegularFile("root", "Test.class"));
    Path actual = mockDirectory("root", files);
    // THEN
    paths.assertIsNotEmptyDirectory(INFO, actual);
  }

  @Test
  void should_fail_if_actual_is_empty() {
    // GIVEN
    Path actual = mockDirectory("root", emptyList());
    // WHEN
    expectAssertionError(() -> paths.assertIsNotEmptyDirectory(INFO, actual));
    // THEN
    verify(failures).failure(INFO, shouldNotBeEmpty());
  }

  @Test
  void should_fail_if_actual_is_null() {
    // GIVEN
    Path actual = null;
    // WHEN
    AssertionError error = expectAssertionError(() -> paths.assertIsNotEmptyDirectory(INFO, actual));
    // THEN
    assertThat(error).hasMessage(actualIsNull());
  }

  @Test
  void should_fail_if_actual_does_not_exist() {
    // GIVEN
    given(nioFilesWrapper.exists(actual)).willReturn(false);
    // WHEN
    expectAssertionError(() -> paths.assertIsNotEmptyDirectory(INFO, actual));
    // THEN
    verify(failures).failure(INFO, shouldExist(actual));
  }

  @Test
  void should_fail_if_actual_exists_but_is_not_directory() {
    // GIVEN
    given(nioFilesWrapper.exists(actual)).willReturn(true);
    given(nioFilesWrapper.isDirectory(actual)).willReturn(false);
    // WHEN
    expectAssertionError(() -> paths.assertIsNotEmptyDirectory(INFO, actual));
    // THEN
    verify(failures).failure(INFO, shouldBeDirectory(actual));
  }

  @Test
  void should_throw_runtime_error_wrapping_caught_IOException() throws IOException {
    // GIVEN
    IOException cause = new IOException();
    given(nioFilesWrapper.exists(actual)).willReturn(true);
    given(nioFilesWrapper.isDirectory(actual)).willReturn(true);
    given(nioFilesWrapper.newDirectoryStream(eq(actual), any())).willThrow(cause);
    // WHEN
    Throwable error = catchThrowable(() -> paths.assertIsNotEmptyDirectory(INFO, actual));
    // THEN
    assertThat(error).isInstanceOf(UncheckedIOException.class)
                     .hasCause(cause);
  }

}
