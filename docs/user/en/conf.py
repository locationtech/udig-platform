import sys, os
sys.path.append(os.path.abspath('../..'))
from common import *

# The suffix of source filenames.
source_suffix = '.rst'

# The encoding of source files.
#source_encoding = 'utf-8'

# The master toctree document.
master_doc = 'index'

# -- Options for HTML output ---------------------------------------------------

html_title='uDig %s User Guide' % release
