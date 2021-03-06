/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.kernel.system;

public enum StreamType {
    OUT("System.out"),

    ERR("System.err");

    private String value;

    private StreamType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
