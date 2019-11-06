/*
 * Copyright 2010 Granule Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.granule.tag.utils;

import com.granule.utils.PathUtils;

import junit.framework.TestCase;

public class PathUtilsTest extends TestCase {
 
  public void testSlash() {	 
	  testClean("/aa", "/aa");
	  testClean("./aa", "aa");
	  testClean("c:/aa", "c:/aa");
	  testClean("c:\\aa//", "c:/aa/");
	  testClean("c:/aa/.\\", "c:/aa/");	
	  testClean("/aa/bb/./cc/test.css", "/aa/bb/cc/test.css");
  }
  
  public void testBack() {
		testClean("/assets/img/1564170993625-99/home-gray-sails.png", "/assets/img/1564170993625-99/home-gray-sails.png");
		testClean("/aa/bbb/ccc/../../ddd//fff", "/aa/ddd/fff");
	  testClean(
	  		"css/fontawesome-pro-5.8.1-web/../../fonts/fontawesome-pro-5.8.1-web/fa-brands-400.eot",
				"fonts/fontawesome-pro-5.8.1-web/fa-brands-400.eot");
	  testClean("../../fonts/fontawesome-pro-5.8.1-web/fa-brands-400.woff2", "fonts/fontawesome-pro-5.8.1-web/fa-brands-400.woff2");
	  testClean("c:/aa/..\\", "c:/");
	  testClean("css/../images/yolko.jpg","images/yolko.jpg");
	  testClean(".\\css/../images/yolko.jpg","images/yolko.jpg");
	  testClean("/css/../images/yolko.jpg","/images/yolko.jpg");
	  testClean("/\\aa/bbb/./ccc/..\\../ddd//fff", "/aa/ddd/fff");
  }
  
  private void testClean(String in, String out) {
	  assertEquals(PathUtils.clean(in), out);
  }
}
