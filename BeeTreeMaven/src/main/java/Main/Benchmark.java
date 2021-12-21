package Main;

import Util.BTree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 1, timeUnit = TimeUnit.SECONDS)
public class Benchmark {

    @State(Scope.Benchmark)
    public static class FullMap {
        List<String> ids;
        List<Car> cars;
        BTree<String, Car> carTree;

        @Setup(Level.Iteration)
        public void generateIdsAndCars(BenchmarkParams params) {
            ids = Benchmark.generateIds(Integer.parseInt(params.getParam("elementCount")));
            cars = Benchmark.generateCars(Integer.parseInt(params.getParam("elementCount")));
        }

        @Setup(Level.Invocation)
        public void fillCarB(BenchmarkParams params) {
            carTree = new BTree<>(Integer.parseInt(params.getParam("degree")));
            putMappings(ids, cars, carTree);
        }
    }

    @Param({"10000", "20000", "40000", "80000"})
    public int elementCount;

    @Param({"2", "3", "4", "5"})
    public int degree;

    List<String> ids;
    List<Car> cars;

    @Setup(Level.Iteration)
    public void generateIdsAndCars() {
        ids = generateIds(elementCount);
        cars = generateCars(elementCount);
    }

    static List<String> generateIds(int count) {
        return new ArrayList<>(CarsGenerator.generateShuffleIds(count));
    }

    static List<Car> generateCars(int count) {
        return new ArrayList<>(CarsGenerator.generateShuffleCars(count));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public BTree<String, Car> insertTree(FullMap fm) {
        BTree<String, Car> carTree = new BTree<>(fm.carTree.degree);
        putMappings(ids, cars, carTree);
        return carTree;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void searchTree(FullMap fullMap) {
        fullMap.ids.forEach(id -> fullMap.carTree.get(id));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void removeTree(FullMap fm){
        fm.ids.forEach(id -> fm.carTree.remove(id));
    }

    public static void putMappings(List<String> ids, List<Car> cars, BTree<String, Car> carTree) {
        for (int i = 0; i < cars.size(); i++) {
            carTree.insert(ids.get(i), cars.get(i));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
