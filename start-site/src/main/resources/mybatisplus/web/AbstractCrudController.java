package packageName.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractCrudController<S extends IService<T>, T, K extends Serializable> {

    protected final S service;

    public AbstractCrudController(S service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public T get(@PathVariable K id) {
        return service.getById(id);
    }

    @GetMapping
    public List<T> listAll(QueryWrapper<T> filtering) {
        return service.list(filtering);
    }

    @GetMapping("/filtering")
    public IPage<T> page(IPage<T> page, QueryWrapper<T> filtering) {
        return service.page(page, filtering);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T save(@RequestBody T t) {
        service.save(t);
        return t;
    }

    @PatchMapping
    public T update(@RequestBody T t) {
        service.updateById(t);
        return t;
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBatch(@RequestBody List<T> t) {
        service.saveBatch(t);
    }

    @PatchMapping("/batch")
    public void update(@RequestBody List<T> t) {
        service.updateBatchById(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable K id) {
        service.removeById(id);
    }

}
