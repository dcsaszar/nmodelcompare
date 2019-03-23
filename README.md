# RaQuN🦝

This repository contains code and data accompanying the thesis.

## NModelCompare (Java)

The code was developed and tested in Eclipse Modeling Tools Version: 2018-09 (4.9.0) Build id: 20180917-1800.

* Open the project in Eclipse IDE.
* To reproduce the micro benchmarks run `src/de/huberlin/informatik/nmodelcompare/measurement/MeasureNwmWeightMicroBenchmark.java`
* To reproduce the KD vector statistics of the appendix run `src/de/huberlin/informatik/nmodelcompare/visualization/ShowVectorStatistics.java`

### Test data

The data sets can be found in `NModelCompare/testdata`:

* `captbaritone_webamp`
* `jeffersonRibeiro_react-shopping-cart`
* `kabirbaidhya_react-todo-app`
* `hospitals`
* `warehouses`
* `random`
* `randomLoose`
* `randomTight`

## testdataGenerator (JavaScript)

* `cd testdataGenerator`
* `npm i`
* `npm start` prints usage instructions
* e.g. `npm start -- chvin/react-tetris` tries to extract models from a ReactJS project

If successful, the extracted models are stored in `testdataGenerator/models`, in `ecore` and `csv` format.
