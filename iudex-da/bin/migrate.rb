#!/opt/bin/jruby
#--
# Copyright (C) 2008-2009 David Kellum
#
# Licensed under the Apache License, Version 2.0 (the "License"); you
# may not use this file except in compliance with the License.  You
# may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.  See the License for the specific language governing
# permissions and limitations under the License.
#++

$LOAD_PATH.unshift File.join( File.dirname(__FILE__), "..", "lib" )

require 'rubygems'

require 'iudex-core/console_logback' 

Logback[ "ActiveRecord" ].level = Logback::DEBUG
Logback.root.level = Logback::DEBUG

require 'iudex-da'

#FIXME flag?: Iudex::DA::CONFIG['database'] = 'iudex_test'

require 'iudex-da/ar'

Iudex::DA::migrate( ARGV[0] && ARGV[0].to_i )
