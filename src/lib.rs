use crate::painter::{EguiPainter, MeshPainterHandler};
use egui::{Event, Mesh};

pub mod painter;
pub mod state;

#[no_mangle]
pub extern "C" fn test(left: usize, right: usize) -> usize {
    left + right
}

#[derive(Debug)]
pub struct Egui {
    pub painter: EguiPainter,
    pub events: Vec<Event>,
}

impl Egui {
    pub fn new(handler: MeshPainterHandler) -> Self {
        Self {
            painter: EguiPainter::new(handler),
            events: vec![],
        }
    }
}

#[no_mangle]
pub extern "C" fn egui_create(handler: MeshPainterHandler) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    // let e = &Egui::new(handler);
    let e = Box::new(Egui::new(handler));
    let e = Box::leak(e);
    println!("return instance at: {:?}", e as *const Egui);
    let handler = e.painter.handler;
    println!("handler: {:?}", handler);
    e
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *const Egui) {
    println!("running(g: {:?})...", g);
    let e = &*g;
    println!("painter: {:?}", e.painter);
    let handler = e.painter.handler;
    e.painter.paint_mesh(&Mesh::default());
    println!("handler: {:?}", handler);
}

#[no_mangle]
// pub unsafe extern "C" fn call_void(f: *const fn() -> ()) {
// pub unsafe extern "C" fn call_void(f: fn() -> ()) {
pub unsafe extern "C" fn call_void(f: *const ()) {
    // println!("function: {:?}", f);
    // (*f)();
    // f();
    let fn_ref: fn() -> () = unsafe { std::mem::transmute(f) };
    fn_ref();
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = test(2, 2);
        assert_eq!(result, 4);
    }

    #[test]
    fn create_run_egui() {
        // let g = egui_create(0xff as MeshPainterHandler);
        // unsafe { egui_run(g) };
        // let g = Egui::new(0xaa as MeshPainterHandler);
        // unsafe { egui_run(&g) };
    }

    #[test]
    fn test_basic_calls() {
        // let f = Box::leak(Box::new(move || {
        //     println!("notification");
        // }));
        // let f = f as *const fn();
        fn function() {
            println!("notification");
        }
        let f = function as *const ();
        // let fn_ref: *const fn() -> () = unsafe { std::mem::transmute(f) };
        // let fn_ref: fn() -> () = unsafe { std::mem::transmute(f) };
        unsafe { call_void(f) };
    }
}
