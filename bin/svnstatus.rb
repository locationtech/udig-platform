#!/usr/bin/ruby

class SVN

def initialize
status=`svn status`
@parsed=status.split(/\n/)
end

def parse (re)
@parsed.each do |line|
if line =~ re
file=$'.strip
yield file
end
end
end

def addAll
parse (/^\?/) do |file|
    puts "Add #{file}? (y)es/(n)o/(i)gnore: "
    input=gets
    puts `svn add #{file}` if input =~ /^[yY]/
    if input =~ /^[iI]/ then
	puts file=~/\w+$/
	puts `svn propset svn:ignore #{$&} #{$`}`
    end
end

end

def rmAll
    rm=false
	parse (/^!/) do
	|file| puts "Delete #{file}? (y)es/(n)o: "
	if gets =~ /^[yY]/
	    puts `svn rm #{file}`
	    rm=true
	end
    end
end

def conflict
parse (/^C/) do |file|
	puts "Conflicted file: #{file} is resolved? (y)es/(n)o: "
	if gets =~ /^[yY]/
		puts `svn resolved #{file}`
	else
		puts "Revert #{file}? (y)es/(n)o: "
		puts `svn revert #{file}` if gets =~ /^[yY]/
	end
end
end

def commit
  puts "Enter message:"
  message=gets
  message.strip
  puts %x{svn commit -m "#{message}" .}
  true if $? == 0
end

def status
  puts `svn status`
end

def update
  puts `svn update`
end
def commit?
exit=false
initialize
exit=true if @parsed.length() == 0
while !exit do
puts "(C)ommit changes/(R)eview Changes/(E)xit:"
ans=gets
ans.strip!

exit=commit if ans =~ /^[cC]/

status if ans =~ /^[rR]/

exit=true unless ans =~ /^[rRcC]/

end
end
end

svn=SVN.new
svn.conflict
svn.rmAll
svn.update
svn.addAll
svn.commit?
