#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# If gradle is available in path, execute it directly
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

# Attempt to locate gradle in typical locations
if [ -n "$GRADLE_HOME" ] && [ -x "$GRADLE_HOME/bin/gradle" ]; then
    exec "$GRADLE_HOME/bin/gradle" "$@"
fi

echo "Gradle is not installed in the current environment. Please install Gradle or use setup-gradle action." >&2
exit 1
