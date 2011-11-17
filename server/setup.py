from distutils.core import setup

setup(
    name="bluelock",
    version="0.1",
    scripts=["bluelock.py"],
    requires=["bluetooth"],
)
