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
pub extern "C" fn egui_create(handler: *const ()) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    let handler: MeshPainterHandler = unsafe { std::mem::transmute(handler) };
    // let e = &Egui::new(handler);
    let e = Box::new(Egui::new(handler));
    let e = Box::leak(e);
    println!("return instance at: {:?}", e as *const Egui);
    e
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *const Egui) {
    println!("running(g: {:?})...", g);
    let e = &*g;
    println!("painter: {:?}", e.painter);
    e.painter.paint_mesh(&Mesh::default());
}

#[no_mangle]
pub unsafe extern "C" fn call_void(f: *const ()) {
    let fn_ref: fn() -> () = unsafe { std::mem::transmute(f) };
    fn_ref();
}

#[no_mangle]
pub unsafe extern "C" fn call_u32(f: *const ()) {
    let fn_ref: fn(u32) -> () = unsafe { std::mem::transmute(f) };
    fn_ref(0x55aa);
}

#[no_mangle]
pub unsafe extern "C" fn call_vec(f: *const ()) {
    let fn_ref: fn(*const u32, u32) -> () = unsafe { std::mem::transmute(f) };
    let vec: Vec<u32> = vec![1, 3, 5, 7, 9];
    let v = Box::leak(Box::new(vec));
    fn_ref(v.as_ptr(), v.len() as u32);
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
        fn function() {
            println!("notification");
        }
        let f = function as *const ();
        unsafe { call_void(f) };
        fn function2(i: u32) {
            println!("recv i: 0x{:x}", i);
        }
        unsafe { call_u32(function2 as *const ()) };
    }
}
