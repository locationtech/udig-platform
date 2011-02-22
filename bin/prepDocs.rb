#!/usr/bin/ruby

require 'ftools'

class ExtPointDocs
    def collect
	index=File.new("doc/extensionPoints/index.html","w")
	index.puts("<html><title=uDig Extension Points Documentation/><body>")
	index.puts("<h2><p>List of uDig's Extension Points:</p></h2>")
	File::cp( Dir["plugins/*/doc/extensionPoints/book.css"].to_a[0], "doc/extensionPoints/", true )
	File::cp( Dir["plugins/*/doc/extensionPoints/schema.css"].to_a[0], "doc/extensionPoints/", true )
	allpoints=Dir["plugins/*/doc/extensionPoints/*.html"].sort
	plugin = ""
	allpoints.each do |filename|
	   if (plugin != /^plugins\/(.+)\/doc/.match(filename)[1] )
		plugin=/^plugins\/(.+)\/doc/.match(filename)[1]
		index.puts("</ul>")
	    index.puts( "<p>Plugin <code>#{plugin}</code> extension points:</p>" )
		index.puts("<ul>")
	   end
	   File::cp( filename, "doc/extensionPoints/", true )
	   match=/\w+\/doc\/extensionPoints\/(\w+).html/.match(filename)
	   htmlname=match[1]
	   name=htmlname.gsub("_",".")
	   index.puts("<li><a href=\"#{htmlname}.html\">#{name}</a></li>")
	end
	index.puts("</ul>")
	index.puts("</body></html>")
	index.close()
    end
    def post
	`scp doc/extensionPoints/* www.refractions.net:/home/www/udig/htdocs/docs/extensionPoints/`
    end
end

class Javadocs
    def collect
    end
    def post
	`scp doc/javadocs/* www.refractions.net:/home/www/udig/htdocs/docs/javadocs/`
    end
end

if Dir["plugins"]
    ep=ExtPointDocs.new
    ep.collect
    ep.post
else
    puts "Must be in the main uDig directory to run this program"
end
