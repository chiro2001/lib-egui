pub mod events;
pub mod painter;
pub mod state;

#[no_mangle]
pub extern "C" fn test(left: usize, right: usize) -> usize {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = test(2, 2);
        assert_eq!(result, 4);
    }
}
