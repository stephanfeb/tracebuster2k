from setuptools import setup

setup(
    name="tracebuster2k",
    version="0.1.0",
    description="A pytest plugin to generate execution traces for tests",
    author="Your Name",
    py_modules=["trace_buster_2k"],
    install_requires=[
        "pytest>=6.0.0",
    ],
    entry_points={
        "pytest11": ["trace_buster_2k = trace_buster_2k"],
    },
    classifiers=[
        "Framework :: Pytest",
    ],
) 