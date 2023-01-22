#[no_mangle]
pub extern "C" fn test(left: usize, right: usize) -> usize {
    left + right
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
    use crate::basic::*;

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
